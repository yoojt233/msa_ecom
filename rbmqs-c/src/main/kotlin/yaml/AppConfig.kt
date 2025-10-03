package service.yaml

data class AppConfig(
    val rabbitmq: RabbitConfig,
    val mongo : MongoConfig
)
