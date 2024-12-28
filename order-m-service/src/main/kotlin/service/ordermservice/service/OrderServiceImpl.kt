package service.ordermservice.service

import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import service.ordermservice.dto.OrderDto
import service.ordermservice.entity.OrderEntity
import service.ordermservice.mq.RabbitProducer
import service.ordermservice.repository.OrderRepository

@Service
class OrderServiceImpl @Autowired constructor(
    val orderRepository: OrderRepository,
    val rabbitProducer: RabbitProducer
) {
    fun createOrder(orderDetails: OrderDto): OrderDto {
        val orderEntity = orderDetails.toOrderEntity()

        // mq
        rabbitProducer.sendQty("qty", orderDetails)

        // jpa
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