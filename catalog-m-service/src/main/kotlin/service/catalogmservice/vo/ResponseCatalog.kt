package service.catalogmservice.vo

import com.fasterxml.jackson.annotation.JsonInclude
import service.catalogmservice.entity.CatalogEntity
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseCatalog(
    val productId: String,
    val productName: String,
    val stock: Int,
    val unitPrice: Int,
    val createdAt: Date
) {
    companion object {
        fun fromCatalogEntity(catalogEntity: CatalogEntity): ResponseCatalog {
            return ResponseCatalog(
                catalogEntity.productId,
                catalogEntity.productName,
                catalogEntity.stock,
                catalogEntity.unitPrice,
                catalogEntity.createdAt
            )
        }
    }
}
