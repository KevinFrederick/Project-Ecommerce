package com.kevinfreyap.core.utils

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object DateHelper {
    @OptIn(ExperimentalTime::class)
    fun parseIsoStringToLong(dateString: String?): Long {
        if (dateString.isNullOrEmpty()) return 0L

        return try {
            Instant.parse(dateString).toEpochMilliseconds()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}