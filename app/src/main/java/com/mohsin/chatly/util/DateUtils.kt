package com.mohsin.chatly.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object {
        fun formatTimestamp(timestamp: Long): CharSequence {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        }
    }
}