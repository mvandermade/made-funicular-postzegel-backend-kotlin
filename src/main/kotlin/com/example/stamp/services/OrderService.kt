package com.example.stamp.services

import com.example.stamp.dtos.OrderDTO
import com.example.stamp.entities.OrderEntity
import com.example.stamp.exceptions.OrderNotFoundV1Exception
import com.example.stamp.mappers.OrderMapper
import com.example.stamp.repositories.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderMapper: OrderMapper,
    private val orderStampService: OrderStampService,
    private val orderIdempotencyKeyService: OrderIdempotencyKeyService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(rollbackFor = [Exception::class])
    fun postOrder(idempotentUserKey: String): OrderDTO {
        val entity =
            orderRepository.save(
                OrderEntity(),
            )
        orderIdempotencyKeyService.saveIdempotencyKey(idempotentUserKey, entity.id)

        return orderMapper.toDTO(entity, entity.orderStampEntity?.stampEntity)
    }

    fun getOrder(orderId: Long): OrderDTO {
        val entity =
            orderRepository.findByIdOrNull(orderId)
                ?: throw OrderNotFoundV1Exception(orderId)

        val updatedStampEntity = entity.orderStampEntity?.stampEntity

        return orderMapper.toDTO(entity, updatedStampEntity)
    }

    fun attemptStampCollection(orderId: Long): OrderDTO {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw OrderNotFoundV1Exception(orderId)

        if (order.orderStampEntity?.stampEntity != null) {
            logger.info("Serving stamp from database")
        } else {
            logger.info("Trying to attach stamp immediately...")
            orderStampService.attachStampsToOrderId(order.id)
        }

        val updatedOrder = orderRepository.findByIdOrNull(orderId) ?: throw OrderNotFoundV1Exception(orderId)
        val updatedStamp = updatedOrder.orderStampEntity?.stampEntity
        return orderMapper.toDTO(updatedOrder, updatedStamp)
    }
}
