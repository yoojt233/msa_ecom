package service.ordermservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import service.ordermservice.entity.OrderEntity

@Repository
interface OrderRepository : CrudRepository<OrderEntity, Long> {
    fun findByOrderId(orderId: String): OrderEntity?
    fun findByUserId(userId: String): Iterable<OrderEntity>
}