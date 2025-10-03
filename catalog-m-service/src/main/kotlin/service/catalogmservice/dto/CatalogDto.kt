package service.catalogmservice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import service.catalogmservice.entity.RdbCatalogEntity
import service.catalogmservice.entity.MongoCatalogEntity
import service.catalogmservice.vo.RequestCatalog
import java.io.Serializable
import java.util.UUID

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CatalogDto(
    val productId: String,
    val productName: String,
    var qty: Int,
    var unitPrice: Int,
    var totalPrice: Long,
) : Serializable {
    fun toCatalogEntity(): RdbCatalogEntity {
        return RdbCatalogEntity(this)
    }

    constructor(requestCatalog: RequestCatalog) : this(
        UUID.randomUUID().toString(),
        requestCatalog.productName,
        requestCatalog.qty,
        requestCatalog.unitPrice,
        requestCatalog.qty * requestCatalog.unitPrice.toLong(),
    )

    constructor(mongoCatalogEntity: MongoCatalogEntity) : this(
        mongoCatalogEntity.productId,
        mongoCatalogEntity.productName,
        mongoCatalogEntity.stock,
        mongoCatalogEntity.unitPrice,
        mongoCatalogEntity.stock * mongoCatalogEntity.unitPrice.toLong(),
    )

    constructor(rdbCatalogEntity: RdbCatalogEntity) : this(
        rdbCatalogEntity.productId,
        rdbCatalogEntity.productName,
        rdbCatalogEntity.stock,
        rdbCatalogEntity.unitPrice,
        rdbCatalogEntity.stock * rdbCatalogEntity.unitPrice.toLong()
    )
}
