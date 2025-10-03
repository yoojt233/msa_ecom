package service.ordermservice.vo

data class ResponseCatalog(
    val productId: String,
    val productName: String,
    val stock: Int,
    val unitPrice: Int,
)
