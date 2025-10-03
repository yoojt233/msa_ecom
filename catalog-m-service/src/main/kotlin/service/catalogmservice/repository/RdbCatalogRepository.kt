package service.catalogmservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import service.catalogmservice.entity.RdbCatalogEntity

@Repository
interface RdbCatalogRepository : CrudRepository<RdbCatalogEntity, Long> {
    fun findByProductId(productId: String): RdbCatalogEntity?
}