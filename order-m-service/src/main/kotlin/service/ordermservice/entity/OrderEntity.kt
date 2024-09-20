package service.ordermservice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import service.ordermservice.dto.OrderDto
import java.io.Serializable
import java.util.Date

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue
    val id: Long,

    @Column(nullable = false, length = 120)
    val productId: String,
    @Column(nullable = false)
    val qty: Int,
    @Column(nullable = false)
    val unitPrice: Int,
    @Column(nullable = false)
    val totalPrice: Int,

    @Column(nullable = false)
    val userId: String,
    @Column(nullable = false, unique = true)
    val orderId: String,

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    val createdAt: Date
) : Serializable {
    constructor(orderDto: OrderDto) : this(
        0L,
        orderDto.productId,
        orderDto.qty,
        orderDto.unitPrice,
        orderDto.totalPrice,
        orderDto.userId,
        orderDto.orderId,
        Date()
    )
}
