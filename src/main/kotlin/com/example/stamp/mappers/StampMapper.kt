package com.example.stamp.mappers

import com.example.stamp.dtos.StampDTO
import com.example.stamp.entities.StampEntity
import org.springframework.stereotype.Component

@Component
class StampMapper {
    fun toDTO(stampEntity: StampEntity): StampDTO {
        return StampDTO(stampEntity.code)
    }
}
