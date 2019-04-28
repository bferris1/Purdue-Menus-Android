package com.moufee.purduemenus.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

class LocalTimeTypeAdapter {

    private val mDateTimeFormatter = DateTimeFormat.forPattern("kk:mm:ss")

    @ToJson
    fun toJson(localTime: LocalTime): String {
        return mDateTimeFormatter.print(localTime)
    }

    @FromJson
    fun fromJson(time: String): LocalTime {
        return mDateTimeFormatter.parseLocalTime(time.substring(0, 8))
    }
}