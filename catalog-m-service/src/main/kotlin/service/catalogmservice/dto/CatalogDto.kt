package service.catalogmservice.dto

import java.io.Serializable

data class CatalogDto(
    var productId: String,
    var qty: Int,
    var unitPrice: Int,
    var totalPrice: Int,

    var orderId: String,
    var userId: String
) : Serializable
