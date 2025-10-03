package service.catalogmservice.mq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.stream.OffsetSpecification
import com.rabbitmq.stream.impl.StreamEnvironmentBuilder
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import service.catalogmservice.service.CatalogService

@Service
class RabbitConsumer(private val catalogService: CatalogService) {
    val objectMapper = ObjectMapper()
    val env = StreamEnvironmentBuilder().build()

    /*
    * protect data loss, set to store offset and start next from last stored
    * must define 'name' to use this function
    */
    @PostConstruct
    fun rollbackConsumer() {
        val stream = "rollback"

        declare(stream)

        env.consumerBuilder()
            .stream(stream)
            .offset(OffsetSpecification.next())
            .name("rollbackConsumer-in-0")
            .manualTrackingStrategy()
            .builder()
            .messageHandler { context, message ->
                runCatching {
                    objectMapper.readValue(message.bodyAsBinary, object : TypeReference<Map<String, Any>>() {})
                }.onSuccess {
                    val productId = it["productId"] as String
                    val qty = it["qty"] as Int

                    catalogService.updateStock(productId, -qty)

                    context.storeOffset()
                }.onFailure { e ->
                    e.printStackTrace()
                }
            }
            .build()
    }

    fun declare(stream: String) {
        if (!env.streamExists(stream)) env.streamCreator().stream(stream).create()
    }
}
