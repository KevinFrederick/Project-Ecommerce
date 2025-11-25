package com.kevinfreyap.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    fun formatMillisToFullDate(millis: Long): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}