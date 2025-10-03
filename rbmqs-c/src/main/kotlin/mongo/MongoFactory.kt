package service.mongo

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import service.yaml.ConfigLoader

class MongoFactory {
    private val mongoConfig = ConfigLoader.config.mongo

    private val host: String = mongoConfig.host
    private val port: Int = mongoConfig.port
    private val username: String = mongoConfig.username
    private val password: String = mongoConfig.password
    private val database: String = mongoConfig.database
    private val collections: List<String> = mongoConfig.collections.split(",").map { it.trim() }.toList()
    private val collectionMap = HashMap<String, MongoCollection<Document>>()

    private var client: MongoClient
    private var db: MongoDatabase

    init {
        val credential = MongoCredential.createCredential(username, database, password.toCharArray())
        println("Credential : $credential")

        client = MongoClients.create(
            MongoClientSettings.builder()
                .applyToClusterSettings { it.hosts(listOf(ServerAddress(host, port))) }
                .credential(credential)
                .build()
        )

        db = client.getDatabase(database)
    }

    fun connectCollections(): HashMap<String, MongoCollection<Document>> {
        collections.forEach { collectionMap[it] = db.getCollection(it) }

        return collectionMap
    }
}
