package service.catalogmservice.vo

import com.fasterxml.jackson.annotation.JsonInclude
import service.catalogmservice.dto.CatalogDto
import service.catalogmservice.entity.RdbCatalogEntity
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseCatalog(
    val productId: String,
    val productName: String,
    val stock: Int,
    val unitPrice: Int,
) {
    constructor(catalogDto: CatalogDto) : this(
        catalogDto.productId,
        catalogDto.productName,
        catalogDto.qty,
        catalogDto.unitPrice
    )
}