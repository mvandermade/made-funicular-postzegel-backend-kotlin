package com.example.stamp.repositories

import com.example.stamp.entities.Stamp
import nl.wykorijnsburger.kminrandom.minRandom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class OrderFullRepositoryTest(
    @Autowired private val orderFullRepository: OrderFullRepository,
    @Autowired private val stampRepository: StampRepository,
    @Autowired private val orderRepository: OrderRepository,
) {
    @BeforeEach
    fun setUp() {
        orderFullRepository.deleteAll()
        stampRepository.deleteAll()
        orderRepository.deleteAll()
    }

    @Test
    fun `EntityGraph is working`() {
        val order = orderFullRepository.save(minRandom())
        val stamp =
            stampRepository.save(
                minRandom<Stamp>().apply {
                    this.order = order
                    this.code = "ABCD"
                },
            )
        order.stamp = stamp
        orderRepository.save(order)

        val fetchedOrder = orderFullRepository.findByIdOrNull(order.id)

        assertThat(fetchedOrder?.stamp?.code).isEqualTo("ABCD")
    }
}
