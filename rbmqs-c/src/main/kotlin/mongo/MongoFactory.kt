package service.mongo

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase

class MongoFactory(
    var host: String = "localhost",
    var port: Int = 27017,
    var username: String = "test",
    var password: String = "test"
) {
    private lateinit var client: MongoClient

    private fun connect(db: String) {
        val credential = MongoCredential.createCredential(username, db, password.toCharArray())
        println("Credential : $credential")

        client = MongoClients.create(
            MongoClientSettings.builder()
                .applyToClusterSettings { it.hosts(listOf(ServerAddress(host, port))) }
                .credential(credential)
                .build()
        )
    }

    fun createDB(db: String): MongoDatabase {
        connect(db)
        return client.getDatabase(db)
    }

    fun createDB(): MongoDatabase {
        return createDB("test")
    }
}