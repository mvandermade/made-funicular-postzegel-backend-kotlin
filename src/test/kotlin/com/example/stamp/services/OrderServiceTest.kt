package com.example.stamp.services

import com.example.stamp.entities.OrderEntity
import com.example.stamp.entities.OrderStampEntity
import com.example.stamp.entities.StampEntity
import com.example.stamp.repositories.OrderIdempotencyKeyRepository
import com.example.stamp.repositories.OrderRepository
import com.example.stamp.repositories.OrderStampRepository
import com.example.stamp.repositories.StampRepository
import com.example.stamp.testutils.buildPostgresContainer
import com.ninjasquad.springmockk.SpykBean
import io.mockk.clearAllMocks
import io.mockk.every
import nl.wykorijnsburger.kminrandom.minRandom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class OrderServiceTest(
    @Autowired val orderService: OrderService,
    @Autowired val orderRepository: OrderRepository,
    @Autowired val stampRepository: StampRepository,
    @Autowired val orderStampRepository: OrderStampRepository,
    @Autowired val orderIdempotencyKeyRepository: OrderIdempotencyKeyRepository,
) {
    @SpykBean
    private lateinit var orderIdempotencyKeyService: OrderIdempotencyKeyService

    @BeforeEach
    fun setUp() {
        orderIdempotencyKeyRepository.deleteAll()
        orderRepository.deleteAll()
        clearAllMocks()
    }

    @Test
    fun `Idempotency key save failure is transactional test`() {
        every { orderIdempotencyKeyService.saveIdempotencyKey("123", any()) } throws Exception("oops")
        assertThrows<Exception> {
            orderService.postOrder(idempotentUserKey = "123")
        }
        assertThat(orderRepository.count()).isEqualTo(0)
    }

    @Test
    fun `Idempotency key is saved ok`() {
        val dto = orderService.postOrder(idempotentUserKey = "123")

        val idpEntity = orderIdempotencyKeyRepository.findByUserKey("123")

        assertThat(idpEntity?.order?.id).isEqualTo(dto.id)
    }

    @Test
    fun `Can collect stamp code from database`() {
        val orderEntity =
            orderRepository.save(
                minRandom<OrderEntity>(),
            )
        val stampEntity =
            stampRepository.save(
                minRandom<StampEntity>().apply {
                    this.code = "ABCD"
                },
            )
        orderStampRepository.save(
            OrderStampEntity(orderEntity, stampEntity),
        )

        val collectedOrder = orderService.attemptStampCollection(orderEntity.id)

        assertThat(collectedOrder.stamp?.code).isEqualTo("ABCD")
    }

    companion object {
        @Container
        @ServiceConnection
        val postgresContainer = buildPostgresContainer()
    }
}
