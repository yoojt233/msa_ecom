package service.catalogmservice.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import service.catalogmservice.dto.CatalogDto
import service.catalogmservice.dto.OrderDto
import service.catalogmservice.service.CatalogService
import service.catalogmservice.vo.RequestCatalog
import service.catalogmservice.vo.ResponseCatalog

@RestController
@RequestMapping("/catalog-m-service")
class CatalogController(val catalogService: CatalogService) {

    @GetMapping("/health-check")
    fun status(request: HttpServletRequest): String {
        return String.format("It's Working in Catalog Service on Port %s", request.serverPort)
    }

    @GetMapping("/catalogs")
    fun getCatalogs(): ResponseEntity<List<ResponseCatalog>> {
        val orderList = catalogService.getAllCatalogs().map { ResponseCatalog(it) }.toList()

        return ResponseEntity.status(HttpStatus.OK).body(orderList)
    }

    @GetMapping("/{productId}/catalog")
    fun getCatalog(@PathVariable productId: String): ResponseEntity<ResponseCatalog> {
        val catalog = catalogService.getCatalog(productId)
        val res = ResponseCatalog(catalog)

        return ResponseEntity.status(HttpStatus.OK).body(res)
    }

    @PostMapping("/catalog")
    fun addCatalog(@RequestBody requestCatalog: RequestCatalog): ResponseEntity<ResponseCatalog> {
        val catalog = catalogService.addCatalog(CatalogDto(requestCatalog))
        val res = ResponseCatalog(catalog)

        return ResponseEntity.status(HttpStatus.OK).body(res)
    }

    @PatchMapping("/catalog")
    fun updateStock(@RequestBody orderDto: OrderDto): ResponseEntity<ResponseCatalog> {
        val catalog = catalogService.updateStock(orderDto.productId, orderDto.qty)
        val res = ResponseCatalog(catalog)

        return ResponseEntity.status(HttpStatus.OK).body(res)
    }

    @PutMapping("/{productId}/catalog")
    fun updateCatalog(
        @PathVariable productId: String,
        @RequestBody requestCatalog: RequestCatalog
    ): ResponseEntity<ResponseCatalog> {
        val catalog = catalogService.updateCatalog(productId, CatalogDto(requestCatalog))
        val res = ResponseCatalog(catalog)

        return ResponseEntity.status(HttpStatus.OK).body(res)
    }
}