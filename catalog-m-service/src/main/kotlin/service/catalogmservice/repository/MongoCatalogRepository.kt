package service.catalogmservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import service.catalogmservice.entity.MongoCatalogEntity

@Repository
interface MongoCatalogRepository : CrudRepository<MongoCatalogEntity, Long> {
    fun findByProductId(productId: String): MongoCatalogEntity?
}