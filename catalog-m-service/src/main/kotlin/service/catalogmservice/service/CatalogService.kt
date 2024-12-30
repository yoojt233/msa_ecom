package service.catalogmservice.service

import service.catalogmservice.entity.CatalogEntity

interface CatalogService {
    fun getAllCatalogs(): Iterable<CatalogEntity>
}