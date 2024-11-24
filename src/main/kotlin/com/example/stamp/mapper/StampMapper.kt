package com.example.stamp.mapper

import com.example.stamp.domain.StampResponse
import com.example.stamp.entity.Stamp
import org.springframework.stereotype.Component

@Component
class StampMapper {
    fun toResponse(stamp: Stamp): StampResponse {
        return StampResponse(stamp.code)
    }
}