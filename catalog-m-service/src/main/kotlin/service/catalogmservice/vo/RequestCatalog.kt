package service.catalogmservice.vo

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RequestCatalog(
    val productName: String,
    val qty: Int,
    val unitPrice: Int
)