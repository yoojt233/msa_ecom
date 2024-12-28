package service.rabbit

interface RabbitConsume : AutoCloseable {
    fun start(db: String, topic: String)
    override fun close()
}