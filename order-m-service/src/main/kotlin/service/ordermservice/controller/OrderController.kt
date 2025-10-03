package service.ordermservice.controller

import io.micrometer.core.annotation.Timed
import jakarta.servlet.http.HttpServletRequest
import org.apache.hc.core5.http.HttpStatus
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import service.ordermservice.dto.OrderDto
import service.ordermservice.service.OrderService
import service.ordermservice.vo.RequestOrder
import service.ordermservice.vo.ResponseOrder

@RestController
@RequestMapping("/order-m-service")
class OrderController(val orderService: OrderService) {
    private val logger = LoggerFactory.getLogger(OrderController::class.java)

    @GetMapping("/health-check")
    @Timed(value = "orders.status", longTask = true)
    fun status(request: HttpServletRequest): String {
        return String.format("It's Working in Order Service on Port %s", request.serverPort)
    }

    @PostMapping("/{userId}/order")
    fun createOrder(
        @PathVariable("userId") userId: String,
        @RequestBody requestOrder: RequestOrder
    ): ResponseEntity<ResponseOrder> {
        logger.info("Before add orders data.")

        val orderDto = OrderDto(requestOrder)

        orderDto.userId = userId
        orderService.createOrder(orderDto)

        logger.info("After added orders data.")

        val res = orderDto.toResponseOrder()

        return ResponseEntity.status(HttpStatus.SC_CREATED).body(res)
    }

    @GetMapping("/{userId}/orders")
    fun getUserOrders(@PathVariable("userId") userId: String): ResponseEntity<List<ResponseOrder>> {
        logger.info("Before retrieve orders data.")

        val orderList = orderService.getOrdersByUserId(userId)
        val res = orderList.map { ResponseOrder(it) }.toList()

        logger.info("After retrieved orders data.")

        return ResponseEntity.status(HttpStatus.SC_OK).body(res)
    }

    @DeleteMapping("/{orderId}/order")
    fun cacelOrder(@PathVariable("orderId") orderId: String): ResponseEntity<ResponseOrder> {
        orderService.cancelOrderByOrderId(orderId)

        return ResponseEntity.status(HttpStatus.SC_OK).build<ResponseOrder>()
    }
}
