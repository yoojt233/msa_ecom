package service.ordermservice.service

import service.ordermservice.dto.OrderDto

interface OrderService {
    fun createOrder(orderDto: OrderDto): OrderDto
    fun getOrderByOrderId(orderId: String): OrderDto
    fun getOrdersByUserId(userId: String): Iterable<OrderDto>
    fun cancelOrderByOrderId(orderId: String)
}