package service.ordermservice.dto

import service.ordermservice.entity.MongoOrderEntity
import service.ordermservice.entity.RdbOrderEntity
import service.ordermservice.vo.RequestOrder
import service.ordermservice.vo.ResponseOrder
import java.io.Serializable
import java.util.UUID

data class OrderDto(
    var productId: String,
    var qty: Int,
    var unitPrice: Int,
    var totalPrice: Int,
    var orderId: String,
    var userId: String
) : Serializable {
    constructor(requestOrder: RequestOrder) : this(
        requestOrder.productId,
        requestOrder.qty,
        requestOrder.unitPrice,
        requestOrder.qty * requestOrder.unitPrice,
        UUID.randomUUID().toString(),
        ""
    )

    fun toOrderEntity(): RdbOrderEntity {
        return RdbOrderEntity(this)
    }

    fun toResponseOrder(): ResponseOrder {
        return ResponseOrder(this)
    }

    companion object {
        fun fromOrderEntity(rdbOrderEntity: RdbOrderEntity): OrderDto {
            return OrderDto(
                rdbOrderEntity.productId,
                rdbOrderEntity.qty,
                rdbOrderEntity.unitPrice,
                rdbOrderEntity.totalPrice,
                rdbOrderEntity.orderId,
                rdbOrderEntity.userId
            )
        }

        fun fromOrderEntity(mongoOrderEntity: MongoOrderEntity): OrderDto {
            return OrderDto(
                mongoOrderEntity.productId,
                mongoOrderEntity.qty,
                mongoOrderEntity.unitPrice,
                mongoOrderEntity.totalPrice,
                mongoOrderEntity.orderId,
                mongoOrderEntity.userId
            )
        }
    }
}
