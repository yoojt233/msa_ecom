package service.ordermservice.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import service.ordermservice.client.CatalogServiceClient
import service.ordermservice.dto.OrderDto
import service.ordermservice.exception.BaseException
import service.ordermservice.exception.ErrorCode
import service.ordermservice.mq.RabbitProducer
import service.ordermservice.repository.MongoOrderRepository
import service.ordermservice.repository.RdbOrderRepository

@Service
class OrderServiceImpl @Autowired constructor(
    private val rdbOrderRepository: RdbOrderRepository,
    private val mongoOrderRepository: MongoOrderRepository,
    private val rabbitProducer: RabbitProducer,
    private val catalogServiceClient: CatalogServiceClient,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>,
) : OrderService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun createOrder(orderDto: OrderDto): OrderDto {
        val orderEntity = orderDto.toOrderEntity()
        val circuitBreaker = circuitBreakerFactory.create("catalogService")

        val catalog = circuitBreaker.run(
            {
                val temp = catalogServiceClient
                    .getCatalog(orderDto.productId) ?: throw BaseException(ErrorCode.PRODUCT_NOT_FOUND)

                if (temp.stock < orderDto.qty) throw BaseException(ErrorCode.QUANTITY_LACK)
                catalogServiceClient.updateStock(orderDto) ?: throw BaseException(ErrorCode.REQUEST_FAILURE)

                // jpa
                logger.info("save Order to PSQL start ...")
                rdbOrderRepository.save(orderEntity)
                logger.info("save Order to PSQL complete ...")
            },
            { _: Throwable ->
                throw BaseException(ErrorCode.OPEN_FEIGN_FAILURE)
            }
        )

        return OrderDto.fromOrderEntity(orderEntity)
    }

    @Transactional(readOnly = true)
    override fun getOrderByOrderId(orderId: String): OrderDto {
//        val orderEntity =
//            rdbOrderRepository.findByOrderId(orderId) ?: throw BaseException(ErrorCode.ORDER_NOT_FOUND)
        val orderEntity = mongoOrderRepository.findByOrderId(orderId) ?: throw BaseException(ErrorCode.ORDER_NOT_FOUND)

        if (!orderEntity.isValid) throw BaseException(ErrorCode.INVALID_VALUE)

        return OrderDto.fromOrderEntity(orderEntity)
    }

    @Transactional(readOnly = true)
    override fun getOrdersByUserId(userId: String): Iterable<OrderDto> {
        val entities = mongoOrderRepository.findAllByUserId(userId)

        return entities.filter { it.isValid }.map { OrderDto.fromOrderEntity(it) }.toList()
    }

    @Transactional
    override fun cancelOrderByOrderId(orderId: String) {
        val order = rdbOrderRepository.findByOrderId(orderId) ?: throw BaseException(ErrorCode.ORDER_NOT_FOUND)

        if (!order.isValid) throw BaseException(ErrorCode.INVALID_VALUE)
        order.deleteOrder()

        val rollBackOrder = OrderDto.fromOrderEntity(order)

        rabbitProducer.sendOrder("rollback", rollBackOrder)
        rdbOrderRepository.save(order)
    }
}
