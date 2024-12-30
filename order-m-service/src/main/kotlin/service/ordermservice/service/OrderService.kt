package service.ordermservice.service

import service.ordermservice.dto.OrderDto
import service.ordermservice.entity.OrderEntity

interface OrderService {
    fun createOrder(orderDto: OrderDto): OrderDto
    fun getOrderByOrderId(orderId: String): OrderDto
    fun getOrdersByUserId(userId: String): Iterable<OrderEntity>
}