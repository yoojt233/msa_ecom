package service.ordermservice.dto

import service.ordermservice.entity.OrderEntity
import service.ordermservice.vo.RequestOrder
import service.ordermservice.vo.ResponseOrder
import java.io.Serializable

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
        0,
        "",
        ""
    )

    fun toOrderEntity(): OrderEntity {
        return OrderEntity(this)
    }

    fun toResponseOrder(): ResponseOrder {
        return ResponseOrder(this)
    }

    companion object {
        fun fromOrderEntity(orderEntity: OrderEntity): OrderDto {
            return OrderDto(
                orderEntity.productId,
                orderEntity.qty,
                orderEntity.unitPrice,
                orderEntity.totalPrice,
                orderEntity.orderId,
                orderEntity.userId
            )
        }
    }
}
