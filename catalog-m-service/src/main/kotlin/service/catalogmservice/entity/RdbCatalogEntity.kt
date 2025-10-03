package service.catalogmservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicUpdate
import service.catalogmservice.dto.CatalogDto
import java.io.Serializable
import java.util.Date

@Entity
@DynamicUpdate
@Table(name = "catalogs")
data class RdbCatalogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false, length = 120, unique = true)
    val productId: String,

    @Column(nullable = false)
    var productName: String,

    @Column(nullable = false)
    var stock: Int,

    @Column(nullable = false)
    var unitPrice: Int,

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    val createdAt: Date
) : Serializable {
    constructor(catalogDto: CatalogDto) : this(
        0L,
        catalogDto.productId,
        catalogDto.productName,
        catalogDto.qty,
        catalogDto.unitPrice,
        Date()
    )

    fun updateName(productName: String) {
        this.productName = productName
    }

    fun updateStock(stock: Int) {
        this.stock = stock
    }

    fun updateUnitPrice(unitPrice: Int) {
        this.unitPrice = unitPrice
    }
}
