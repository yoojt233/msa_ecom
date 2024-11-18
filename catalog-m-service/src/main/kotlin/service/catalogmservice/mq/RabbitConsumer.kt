package service.catalogmservice.mq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.stream.Environment
import com.rabbitmq.stream.OffsetSpecification
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import service.catalogmservice.repository.CatalogRepository

@Service
class RabbitConsumer(private val catalogRepository: CatalogRepository) {
    val objectMapper = ObjectMapper()
    val env = Environment.builder().build()

    /*
    * protect data loss, set to store offset and start next from last stored
    * must define 'name' to use this function
    */
    @PostConstruct
    fun updateQty() {
        val stream = "qty_test"

        declare(stream)

        val consumer = env.consumerBuilder()
            .stream(stream)
            .offset(OffsetSpecification.next())
            .name("qty_test")
            .manualTrackingStrategy()
            .builder()
            .messageHandler { context, message ->
                kotlin.runCatching {
                    objectMapper.readValue(message.bodyAsBinary, object : TypeReference<Map<String, Any>>() {})
                }.onSuccess {
                    val entity = catalogRepository.findByProductId(it["productId"] as String)

                    if (entity != null) {
                        entity.stock -= it["qty"] as Int
                        catalogRepository.save(entity)
                    }

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
