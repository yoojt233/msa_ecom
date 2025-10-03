package service.ordermservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import service.ordermservice.dto.OrderDto
import service.ordermservice.vo.ResponseCatalog

@FeignClient(name = "catalog-m-service")
interface CatalogServiceClient {

    @GetMapping("/catalog-m-service/{productId}/catalog")
    fun getCatalog(@PathVariable productId: String): ResponseCatalog?

    @PatchMapping("/catalog-m-service/catalog")
    fun updateStock(@RequestBody orderDto: OrderDto): ResponseCatalog?
}
