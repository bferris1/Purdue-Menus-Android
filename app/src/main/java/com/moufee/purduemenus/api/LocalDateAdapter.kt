package com.moufee.purduemenus.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class LocalDateAdapter {

    private val formatter = DateTimeFormat.forPattern("M/d/yyyy")

    @ToJson
    fun toJson(date: LocalDate): String {
        return date.toString(formatter)
    }

    @FromJson
    fun fromJson(date: String): LocalDate {
        return LocalDate.parse(date, formatter)
    }
}