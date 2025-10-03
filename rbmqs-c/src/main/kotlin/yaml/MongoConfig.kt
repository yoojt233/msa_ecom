package service.yaml

data class MongoConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val database: String,
    val collections: String
)
