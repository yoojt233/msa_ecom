package service.usermservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import service.usermservice.vo.ResponseOrder

@FeignClient(name = "order-m-service")
interface OrderServiceClient {

    @GetMapping("/order-m-service/{userId}/orders")
    fun getOrders(@PathVariable userId: String): List<ResponseOrder>
}