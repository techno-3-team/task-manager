package com.techno_3_team.task_manager_google.data

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): com.techno_3_team.task_manager_google.support.Date? {
        if (value == null) {
            return null
        }
        val dateArray = value.split(" ")
        return com.techno_3_team.task_manager_google.support.Date(
            dateArray[0].toInt(),
            dateArray[1].toInt(),
            dateArray[2].toInt(),
            dateArray[3].toInt(),
            dateArray[4].toInt())
    }

    @TypeConverter
    fun dateToTimestamp(date: com.techno_3_team.task_manager_google.support.Date?): String? {
        if (date == null) {
            return null
        }
        val sb = StringBuilder()
        sb.append("${date.year} ${date.month} ${date.day} ${date.hour} ${date.minute}")
        return sb.toString()
    }
}