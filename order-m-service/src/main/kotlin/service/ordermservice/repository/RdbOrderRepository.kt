package service.ordermservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import service.ordermservice.entity.RdbOrderEntity

@Repository
interface RdbOrderRepository : CrudRepository<RdbOrderEntity, Long> {
    fun findByOrderId(orderId: String): RdbOrderEntity?
    fun findByUserId(userId: String): Iterable<RdbOrderEntity>
}