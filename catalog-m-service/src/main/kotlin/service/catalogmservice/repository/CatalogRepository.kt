package service.catalogmservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import service.catalogmservice.entity.CatalogEntity

@Repository
interface CatalogRepository : CrudRepository<CatalogEntity, Long> {
    fun findByProductId(productId: String): CatalogEntity?
}