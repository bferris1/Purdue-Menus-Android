package com.moufee.purduemenus.api

import android.content.Context
import android.util.Log
import com.moufee.purduemenus.menus.FullDayMenu
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.*
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "MenuCache"

@Singleton
class MenuCache @Inject constructor(val context: Context) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    fun get(dateTime: DateTime): FullDayMenu? {
        val date = formatter.print(dateTime)
        val filename = "$date.fdm"
        val filesDir = context.cacheDir
        val sourceFile = File(filesDir, filename)
        val result: FullDayMenu
        if (!sourceFile.exists())
            return null
        try {
            val fileInputStream = FileInputStream(sourceFile)
            val objectInputStream = ObjectInputStream(fileInputStream)
            result = objectInputStream.readObject() as FullDayMenu
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: Exception) {
            Log.e(TAG, "get: error", e)
            return null
        }
        return result
    }

    //todo: unchecked exception?
    @Synchronized
    @Throws(IOException::class)
    fun put(menu: FullDayMenu) {
        val date = formatter.print(menu.date)
        val filename = "$date.fdm"
        val filesDir = context.cacheDir
        val outputFile = File(filesDir, filename)
        val fileOutputStream = FileOutputStream(outputFile)
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(menu)
        objectOutputStream.close()
        fileOutputStream.close()
    }
}