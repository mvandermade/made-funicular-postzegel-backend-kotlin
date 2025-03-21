package com.example.stamp.dtos

import java.time.OffsetDateTime

data class StampReportDTO(
    val id: Long,
    val code: String,
    val createdAtServer: OffsetDateTime,
    val createdAtObserver: OffsetDateTime?,
    val reachedDestination: Boolean?,
    val comment: String?,
)
