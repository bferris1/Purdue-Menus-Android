package com.moufee.purduemenus.api

import android.content.Context
import com.moufee.purduemenus.repository.data.menus.DayMenu
import com.squareup.moshi.Moshi
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MenuCache @Inject constructor(val context: Context, val moshi: Moshi) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
    private val jsonAdapter = moshi.adapter(DayMenu::class.java)

    fun get(dateTime: LocalDate): DayMenu? {
        val date = formatter.print(dateTime)
        val filename = "$date.day-menu.json"
        val filesDir = context.cacheDir
        val sourceFile = File(filesDir, filename)
        if (!sourceFile.exists())
            return null
        return FileReader(sourceFile).use { fileReader ->
            fileReader.readText()
        }.let { jsonAdapter.fromJson(it) }

    }

    //todo: unchecked exception?
    @Synchronized
    @Throws(IOException::class)
    fun put(menu: DayMenu) {
        val date = formatter.print(menu.date)
        val filename = "$date.day-menu.json"
        val filesDir = context.cacheDir
        val outputFile = File(filesDir, filename)
        FileWriter(outputFile).use {
            it.write(jsonAdapter.toJson(menu))
        }
    }
}