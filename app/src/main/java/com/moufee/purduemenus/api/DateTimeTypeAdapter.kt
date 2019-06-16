package com.moufee.purduemenus.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class DateTimeTypeAdapter {

    private val mDateTimeFormatter = DateTimeFormat.forPattern("kk:mm:ss")

    @ToJson
    fun toJson(localTime: DateTime): String {
        return mDateTimeFormatter.print(localTime)
    }

    @FromJson
    fun fromJson(time: String): DateTime {
        return mDateTimeFormatter.parseDateTime(time)
    }
}