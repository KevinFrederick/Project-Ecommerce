package com.kevinfreyap.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {

    fun formatTransactionTime(millis: Long): String{
        val instant = Instant.ofEpochMilli(millis)
        val zoneId = ZoneId.systemDefault()

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - hh:mm a", Locale.getDefault())

        return instant.atZone(zoneId).format(formatter)
    }
}