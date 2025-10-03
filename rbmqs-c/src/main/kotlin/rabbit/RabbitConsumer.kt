package service.rabbit

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.rabbitmq.stream.Consumer
import com.rabbitmq.stream.Environment
import com.rabbitmq.stream.OffsetSpecification
import org.bson.Document
import service.mongo.MongoFactory
import service.yaml.ConfigLoader

class RabbitConsumer(private val env: Environment) {
    private val objectMapper = ObjectMapper()
    private val consumers = mutableMapOf<String, Consumer>()
    private val rabbitConfig = ConfigLoader.config.rabbitmq
    private val collections = MongoFactory().connectCollections()

    fun start() {
        val topic = rabbitConfig.stream.topic

        println("Starting Consuming from Queue : $topic")

        if (consumers.contains(topic)) return
        if (!env.streamExists(topic)) env.streamCreator().stream(topic).create()

        val consumer = env.consumerBuilder()
            .stream(topic)
            .offset(OffsetSpecification.next())
            .name("$topic-consumer")
            .autoTrackingStrategy()
            .builder()
            .messageHandler { context, message ->
                runCatching {
                    objectMapper.readValue(message.bodyAsBinary, object : TypeReference<Map<String, Any?>>() {})
                }.onSuccess {
                    val payload = it["payload"] as Map<String, *>
                    val after = payload["after"] as Map<String, *>
                    val source = payload["source"] as Map<String, *>
                    val table = source["table"] as String
                    val collection = collections[table]!!

                    when (payload["op"] as String) {
                        "c" -> {
                            val doc = Document()
                            for (key in after.keys) doc.append(if (key == "id") "_id" else key, after[key])

                            collection.insertOne(doc)
                        }

                        "u" -> {
                            val doc = Filters.eq("_id", after["id"])
                            val updateList = after.map { (key, value) ->
                                Updates.set(if (key == "id") "_id" else key, value)
                            }
                            val updateOperation = Updates.combine(updateList)
                            val options = UpdateOptions().upsert(true)

                            collection.updateOne(doc, updateOperation, options)
                        }

                        "d" -> {
                            val before = payload["before"] as Map<String, *>
                            val doc = Filters.eq("_id", before["id"])

                            collection.deleteOne(doc)
                        }

                        else -> println("Check Rabbitmq message IMMEDIATELY!")
                    }

                    context.storeOffset()
                }.onFailure { e ->
                    e.printStackTrace()
                }
            }
            .build()

        consumers[topic] = consumer
    }

    fun close() {
        consumers.values.forEach { it.close() }
        env.close()
    }
}
