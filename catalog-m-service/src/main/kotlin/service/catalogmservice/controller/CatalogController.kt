package service.catalogmservice.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import service.catalogmservice.service.CatalogServiceImpl
import service.catalogmservice.vo.ResponseCatalog

@RestController
@RequestMapping("/catalog-m-service")
class CatalogController(val catalogServiceImpl: CatalogServiceImpl) {

    @GetMapping("/health-check")
    fun status(request: HttpServletRequest): String {
        return String.format("It's Working in Catalog Service on Port %s", request.serverPort)
    }

    @GetMapping("/catalogs")
    fun getCatalogs(): ResponseEntity<List<ResponseCatalog>> {
        val orderList = catalogServiceImpl.getAllCatalogs().map { ResponseCatalog.fromCatalogEntity(it) }.toList()

        return ResponseEntity.status(HttpStatus.OK).body(orderList)
    }
}