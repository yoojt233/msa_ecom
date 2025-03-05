package service.ordermservice

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import jakarta.ws.rs.NotFoundException
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import service.ordermservice.dto.OrderDto
import service.ordermservice.service.OrderService
import service.ordermservice.vo.RequestOrder
import java.util.*

@WebMvcTest
class OrderControllerTests(
    @MockBean private val orderService: OrderService,
    val mockMvc: MockMvc
) : BehaviorSpec({

    given("checking server health") {
        `when`("server run healthy") {
            val res = mockMvc.perform(
                MockMvcRequestBuilders.get("/order-m-service/health-check")
            )

            then("the service is healthy") {
                res
                    .andDo(MockMvcResultHandlers.print())
            }
        }
    }

    // TODO: fix any() method. It doesn't work normally now. Have to check "matcher"

//    given("userId and want some behavior about orders") {
//        val requestOrder = RequestOrder("CATALOG-0001", 5, 900)
//        val orderDto = OrderDto(requestOrder)
//
//        orderDto.userId = "User No.1"
//        orderDto.orderId = UUID.randomUUID().toString()
//        orderDto.totalPrice = requestOrder.qty * requestOrder.unitPrice
//
//        `when`("create order successfully") {
//            val x = jacksonObjectMapper().writeValueAsString(orderDto)
//            println(x)
//
//            every { orderService.createOrder(any()) } returns orderDto
//
//            val res = mockMvc.perform(
//                MockMvcRequestBuilders
//                    .post("/order-m-service/{userId}/orders", orderDto.userId)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(jacksonObjectMapper().writeValueAsString(orderDto))
//            )
//
//            then("return isCreated status") {
//                res
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(status().isCreated)
//            }
//        }
//
//        `when`("cannot find product") {
//            every { orderService.createOrder(any()) }.throws(NotFoundException("There is no ProductId : ${requestOrder.productId}"))
//
//            val res = mockMvc.perform(
//                MockMvcRequestBuilders
//                    .post("/order-m-service/{userId}/orders", orderDto.userId)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(jacksonObjectMapper().writeValueAsString(orderDto))
//            )
//
//            then("return isNotFound status") {
//                res
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(status().isNotFound)
//            }
//        }
//
//        `when`("GET request with userId") {
//            every { orderService.getOrdersByUserId(any()) } returns listOf()
//
//            val res = mockMvc.perform(
//                MockMvcRequestBuilders.get("/{userId}/orders", orderDto.userId)
//            )
//
//            then("return order list") {
//                res
//                    .andExpect(status().isOk)
//                    .andDo(MockMvcResultHandlers.print())
//            }
//        }
//}
})
