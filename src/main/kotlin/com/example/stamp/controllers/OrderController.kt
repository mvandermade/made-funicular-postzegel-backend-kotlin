package com.example.stamp.controllers

import com.example.stamp.mappers.ExceptionMapper
import com.example.stamp.mappers.OrderMapper
import com.example.stamp.services.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/orders")
class OrderController(
    private val orderService: OrderService,
    private val exceptionMapper: ExceptionMapper,
    private val orderMapper: OrderMapper,
) {
    // TODO expand this to also accept more interesting things. For now we order 1 stamp.
    @PostMapping
    fun requestOrder() =
        ResponseEntity.ok(
            orderMapper.toResponse(
                orderService.requestOrder(),
            ),
        )
}
