package service.catalogmservice.service

import service.catalogmservice.dto.CatalogDto

interface CatalogService {
    fun getAllCatalogs(): Iterable<CatalogDto>
    fun getCatalog(productId: String): CatalogDto
    fun addCatalog(catalogDto: CatalogDto): CatalogDto
    fun updateStock(productId: String, cnt: Int): CatalogDto
    fun updateCatalog(productId: String, catalogDto: CatalogDto): CatalogDto
}