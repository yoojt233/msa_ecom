package service.rabbit

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.rabbitmq.stream.Environment
import com.rabbitmq.stream.OffsetSpecification
import org.bson.Document
import service.mongo.MongoFactory

class RabbitConsumeImpl : RabbitConsume {
    private val env = Environment.builder().build()
    private val objectMapper = ObjectMapper()

    override fun start(db: String, table: String) {
        println("Wait for connect DB...")
        val targetDB = MongoFactory(username = "esta", password = "zxcv3210").createDB(db)
        val collection = targetDB.getCollection(table)

        val topic = "$db.$table"
        println("Starting Consuming from Queue : $topic")

        val consumer = env.consumerBuilder()
            .stream(topic)
            .offset(OffsetSpecification.next())
            .name("$topic-consumer")
            .manualTrackingStrategy()
            .builder()
            .messageHandler { context, message ->
                runCatching {
                    objectMapper.readValue(message.bodyAsBinary, object : TypeReference<Map<String, Any>>() {})
                }.onSuccess {
                    val payload = it["payload"] as Map<String, Any>
                    val before = payload["before"] as Map<String, Any>?
                    val after = payload["after"] as Map<String, Any>?
                    val op = payload["op"] as String?
                    val transaction = payload["transaction"] as String?

                    when (op) {
                        "c" -> {
                            val doc = Document()
                            for (key in after!!.keys) {
                                if (key == "id") doc.append("_id", after[key] as Int) else doc.append(key, after[key])
                            }

                            collection.insertOne(doc)
                        }

                        "u" -> {
                            val doc = Filters.eq("_id", after!!["id"])
                            val updateList = after!!.map { (key, value) ->
                                Updates.set(if (key == "id") "_id" else key, value)
                            }
                            val updateOperation = Updates.combine(updateList)

                            collection.updateOne(doc, updateOperation)
                        }

                        "d" -> {
                            val doc = Filters.eq("_id", before!!["id"])

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
    }

    override fun close() {
        env.close()
    }
}
