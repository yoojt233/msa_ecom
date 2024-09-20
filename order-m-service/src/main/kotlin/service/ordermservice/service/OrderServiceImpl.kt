package service.ordermservice.service

import jakarta.ws.rs.NotFoundException
import org.springframework.stereotype.Service
import service.ordermservice.dto.OrderDto
import service.ordermservice.entity.OrderEntity
import service.ordermservice.repository.OrderRepository
import java.util.*

@Service
class OrderServiceImpl(val orderRepository: OrderRepository) {
    fun createOrder(orderDetails: OrderDto): OrderDto {
        orderDetails.orderId = UUID.randomUUID().toString()
        orderDetails.totalPrice = orderDetails.qty * orderDetails.unitPrice

        val orderEntity = orderDetails.toOrderEntity()

        orderRepository.save(orderEntity)

        return OrderDto.fromOrderEntity(orderEntity)
    }

    fun getOrderByOrderId(orderId: String): OrderDto {
        val orderEntity =
            orderRepository.findByOrderId(orderId) ?: throw NotFoundException("There is no order with id $orderId")

        return OrderDto.fromOrderEntity(orderEntity)
    }

    fun getOrdersByUserId(userId: String): Iterable<OrderEntity> {
        return orderRepository.findByUserId(userId)
    }
}