package com.example.stamp.controllers

import com.example.stamp.controllers.responses.StampResponse
import com.example.stamp.entities.Order
import com.example.stamp.entities.Stamp
import com.example.stamp.repositories.OrderRepository
import com.example.stamp.repositories.StampRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class StampControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val orderRepository: OrderRepository,
    @Autowired private val stampRepository: StampRepository,
) {
    @Test
    fun `Should get a stamp after waiting a bit`() {
        val order = orderRepository.save(Order().apply { orderConfirmed = true })
        val stamp = stampRepository.save(Stamp())

        val result =
            mockMvc.perform(get("$PATH/collect/${order.id}"))
                .andExpect(status().isOk)
                .andReturn().let { objectMapper.readValue<StampResponse>(it.response.contentAsString) }

        assertThat(stamp.code).isEqualTo(result.code)
    }

    companion object {
        const val PATH = "/v1/stamps"
    }
}
