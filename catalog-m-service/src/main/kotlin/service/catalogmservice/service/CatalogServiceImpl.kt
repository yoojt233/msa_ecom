package service.catalogmservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import service.catalogmservice.dto.CatalogDto
import service.catalogmservice.repository.MongoCatalogRepository
import service.catalogmservice.repository.RdbCatalogRepository

@Service
class CatalogServiceImpl @Autowired constructor(
    private val rdbCatalogRespository: RdbCatalogRepository,
    private val mongoCatalogRepository: MongoCatalogRepository
) : CatalogService {

    @Transactional(readOnly = true)
    override fun getAllCatalogs(): Iterable<CatalogDto> {
        val entities = mongoCatalogRepository.findAll()

        return entities.map { CatalogDto(it) }.toList()
    }

    @Transactional(readOnly = true)
    override fun getCatalog(productId: String): CatalogDto {
        val entity =
            mongoCatalogRepository.findByProductId(productId) ?: throw Exception("Can't find matching productID.")

        return CatalogDto(entity)
    }

    @Transactional
    override fun addCatalog(catalogDto: CatalogDto): CatalogDto {
        val entity = catalogDto.toCatalogEntity()

        rdbCatalogRespository.save(entity)

        return CatalogDto(entity)
    }

    @Transactional
    override fun updateStock(productId: String, cnt: Int): CatalogDto {
        val entity = rdbCatalogRespository.findByProductId(productId) ?: throw Exception("Not Found Catalog.")

        entity.updateStock(entity.stock - cnt)
        rdbCatalogRespository.save(entity)

        return CatalogDto(entity)
    }

    @Transactional
    override fun updateCatalog(productId: String, catalogDto: CatalogDto): CatalogDto {
        val entity = rdbCatalogRespository.findByProductId(productId) ?: throw Exception("Not Found Catalog.")

        entity.updateName(catalogDto.productName)
        entity.updateStock(catalogDto.qty)
        entity.updateUnitPrice(catalogDto.unitPrice)

        rdbCatalogRespository.save(entity)

        return CatalogDto(entity)
    }
}
