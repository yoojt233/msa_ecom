package service.usermservice.vo

import java.util.Date

data class ResponseOrder(
    val productId: String,
    val qty: Int,
    val unitPrice: Int,
    val totalPrice: Int,
    val createdAt: Date,

    val orderId: String
)
