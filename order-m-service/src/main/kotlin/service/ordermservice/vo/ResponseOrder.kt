package service.ordermservice.vo

import com.fasterxml.jackson.annotation.JsonInclude
import service.ordermservice.dto.OrderDto
import service.ordermservice.entity.OrderEntity
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseOrder(
    val productId: String,
    val qty: Int,
    val unitPrice: Int,
    val totalPrice: Int,
    val createdAt: Date,

    val orderId: String
) {
    constructor(orderDto: OrderDto) : this(
        orderDto.productId,
        orderDto.qty,
        orderDto.unitPrice,
        orderDto.totalPrice,
        Date(),
        orderDto.orderId
    )

    constructor(orderEntity: OrderEntity) : this(
        orderEntity.productId,
        orderEntity.qty,
        orderEntity.unitPrice,
        orderEntity.totalPrice,
        orderEntity.createdAt,
        orderEntity.orderId
    )
}
