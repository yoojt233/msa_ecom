package service.catalogmservice.service

import org.springframework.stereotype.Service
import service.catalogmservice.entity.CatalogEntity
import service.catalogmservice.repository.CatalogRepository

@Service
class CatalogServiceImpl(val catalogRespository: CatalogRepository) {
    fun getAllCatalogs(): Iterable<CatalogEntity> {
        return catalogRespository.findAll()
    }
}