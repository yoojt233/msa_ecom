package service.catalogmservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.io.Serializable
import java.util.Date

@Entity
@Table(name = "catalogs")
data class CatalogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false, length = 120, unique = true)
    val productId: String,

    @Column(nullable = false)
    val productName: String,

    @Column(nullable = false)
    var stock: Int,

    @Column(nullable = false)
    val unitPrice: Int,

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    val createdAt: Date

) : Serializable
