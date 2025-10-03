package service.catalogmservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "catalogs")
data class MongoCatalogEntity(

    @Id
    val id: Long,
    @Field("product_id")
    val productId: String,
    @Field("product_name")
    val productName: String,
    @Field("stock")
    val stock: Int,
    @Field("unit_price")
    val unitPrice: Int,
    @Field("created_at")
    val createdAt: String
)
