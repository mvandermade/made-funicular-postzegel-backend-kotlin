package com.example.stamp.controllers.requests

import java.time.OffsetDateTime

data class ReportRequest(
    val code: String,
    val offsetDateTime: OffsetDateTime?,
    val reachedDestination: Boolean?,
    val comment: String?,
)
