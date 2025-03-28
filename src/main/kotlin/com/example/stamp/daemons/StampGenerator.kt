package com.example.stamp.daemons

import com.example.stamp.providers.TransactionProvider
import com.example.stamp.services.StampService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class StampGenerator(
    private val transactionProvider: TransactionProvider,
    private val stampService: StampService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1_000, initialDelay = 2_000)
    fun insertCodes() {
        try {
            transactionProvider.newReadWrite {
                stampService.persistRandomStamp()
            }
        } catch (e: Exception) {
            if (e.message?.contains("Unique index or primary key violation:") == true) {
                logger.info("Random stamp was duplicated")
            } else {
                e.printStackTrace()
            }
        }
    }
}
