package com.example.stamp.exceptions

enum class ErrorCode {
    STAMP_CODE_REPORT_NOT_FOUND,
    STAMP_NOT_FOUND,
    STAMP_IS_CONFIRMED,
    STAMP_COLLECTION_IN_PROGRESS,
    ORDER_IDEMPOTENCY_KEY_NOT_FOUND,
    ORDER_IDEMPOTENCY_KEY_IN_USE,
    ORDER_NOT_FOUND,
    MISSING_VALUE,
    UNKNOWN_ERROR,
}
