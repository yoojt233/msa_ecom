package service.ordermservice.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import service.ordermservice.dto.OrderDto
import service.ordermservice.service.OrderServiceImpl
import service.ordermservice.vo.RequestOrder
import service.ordermservice.vo.ResponseOrder
import java.util.*

@RestController
@RequestMapping("/order-m-service")
class OrderController(val orderServiceImpl: OrderServiceImpl) {

    @GetMapping("/health-check")
    fun status(request: HttpServletRequest): String {
        return String.format("It's Working in Order Service on Port %s", request.serverPort)
    }

    @PostMapping("/{userId}/orders")
    fun createOrder(
        @PathVariable("userId") userId: String,
        @RequestBody orderDetails: RequestOrder
    ): ResponseEntity<ResponseOrder> {
        val orderDto = OrderDto(orderDetails)

        orderDto.userId = userId
        orderDto.orderId = UUID.randomUUID().toString()
        orderDto.totalPrice = orderDetails.qty * orderDetails.unitPrice

        orderServiceImpl.createOrder(orderDto)

        val res = orderDto.toResponseOrder()

        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    @GetMapping("/{userId}/orders")
    fun getOrder(@PathVariable("userId") userId: String): ResponseEntity<List<ResponseOrder>> {
        val orderList = orderServiceImpl.getOrdersByUserId(userId)
        val res = orderList.map { ResponseOrder(it) }.toList()

        return ResponseEntity.status(HttpStatus.OK).body(res)
    }
}