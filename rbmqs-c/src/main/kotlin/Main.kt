package service

import service.rabbit.RabbitConsumeImpl

fun main() {
    val table = "orders"
    val db = "ecom"
    val rabbit = RabbitConsumeImpl()

    rabbit.start(db, table)
}
