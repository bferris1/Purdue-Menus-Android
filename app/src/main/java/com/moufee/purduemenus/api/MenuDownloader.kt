package com.moufee.purduemenus.api

import android.content.Context
import com.moufee.purduemenus.menus.DiningCourtMenu
import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class MenuDownloader constructor(val webservice: Webservice, private val context: Context) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    fun getMenus(date: DateTime, locations: List<Location>) {
        val locationNames = locations.map { it.Name }
        val retrievedMenus = ArrayList<DiningCourtMenu>()
        var numFailures = 0
        for (location in locationNames) {
            webservice.getMenu(location, formatter.print(date)).enqueue(object : Callback<DiningCourtMenu?> {
                override fun onFailure(call: Call<DiningCourtMenu?>, t: Throwable) {
                    // handle some failure when others complete successfully
                    numFailures++
                    if (retrievedMenus.size + numFailures == locations.size) {
                        // all calls have completed or failed
                        onComplete(FullDayMenu(retrievedMenus, date))
                    }
                }

                override fun onResponse(call: Call<DiningCourtMenu?>, response: Response<DiningCourtMenu?>) {
                    response.body()?.let { retrievedMenus.add(it) }
                    if (retrievedMenus.size + numFailures == locations.size) {
                        // all calls have completed or failed
                        onComplete(FullDayMenu(retrievedMenus, date))
                    }
                }
            })
        }
    }

    abstract fun onComplete(fullDayMenu: FullDayMenu)

    abstract fun onFailure(reason: String)

}