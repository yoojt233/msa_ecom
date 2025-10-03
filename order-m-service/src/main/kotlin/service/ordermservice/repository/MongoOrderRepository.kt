package service.ordermservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import service.ordermservice.entity.MongoOrderEntity

@Repository
interface MongoOrderRepository : CrudRepository<MongoOrderEntity, Long> {
    fun findByOrderId(orderId: String): MongoOrderEntity?
    fun findAllByUserId(userId: String): Iterable<MongoOrderEntity>
}