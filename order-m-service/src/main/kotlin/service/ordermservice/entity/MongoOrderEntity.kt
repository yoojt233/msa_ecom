package service.ordermservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

@Document(collection = "orders")
data class MongoOrderEntity(

    @Id
    val id: Long,
    @Field("product_id")
    val productId: String,
    @Field("qty")
    val qty: Int,
    @Field("unit_price")
    val unitPrice: Int,
    @Field("total_price")
    val totalPrice: Int,
    @Field("user_id")
    val userId: String,
    @Field("order_id")
    val orderId: String,
    @Field("created_at")
    val createdAt: String,
    @Field("is_valid")
    val isValid: Boolean
) : Serializable
