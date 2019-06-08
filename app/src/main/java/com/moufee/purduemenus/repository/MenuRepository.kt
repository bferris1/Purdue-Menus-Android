package com.moufee.purduemenus.repository

import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.moufee.purduemenus.api.MenuCache
import com.moufee.purduemenus.api.MenuDownloader

import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.db.LocationDao
import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import com.moufee.purduemenus.util.AppExecutors
import com.moufee.purduemenus.util.Resource

import org.joda.time.DateTime

import java.io.IOException

import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by Ben on 22/07/2017.
 * Where all the menus live
 * Abstracts the data sources from the rest of the app
 * Creates threads to retrieve and process menu data
 */
@Singleton
class MenuRepository @Inject
constructor(private val mWebservice: Webservice, private val mAppExecutors: AppExecutors, private val mLocationDao: LocationDao, private val menuDownloader: MenuDownloader, private val menuCache: MenuCache) {

    val locations: LiveData<List<Location>>
        get() {
            updateLocationsFromNetwork()
            return mLocationDao.getAll()
        }

    // the first time this is called when the app is first installed (has no data) it will emit an empty list
    // this empty list gets sent
    val visibleLocations: LiveData<List<Location>>
        get() {
            updateLocationsFromNetwork()
            return mLocationDao.getVisible()
        }

    fun getMenus(dateTime: DateTime, locations: List<Location>?): LiveData<Resource<FullDayMenu>> {
        val data = MutableLiveData<Resource<FullDayMenu>>()
        if (locations == null)
            return data
        return liveData {

            try {
                val fileMenus = menuCache.get(dateTime)
                if (fileMenus != null) {
                    emit(Resource.success<FullDayMenu>(fileMenus))
                    Log.d(TAG, "getFullMenu: Read from file!")
                } else {
                    emit(Resource.loading<FullDayMenu>(null))
                }
            } catch (t: Throwable) {
                emit(Resource.loading<FullDayMenu>(null))
                Log.e(TAG, "Error reading from cache.", t)
            }
            try {
                val fullMenu = menuDownloader.getMenus(dateTime, locations)
                emit(Resource.success(fullMenu))
                menuCache.put(fullMenu)
            } catch (t: Throwable) {
                Log.e(TAG, "Network or Write Error", t)
            }

        }
    }

    fun updateLocations(vararg locations: Location) {
        mAppExecutors.diskIO().execute { mLocationDao.updateLocations(*locations) }
    }

    fun updateLocations(locations: List<Location>) {
        mAppExecutors.diskIO().execute { mLocationDao.updateLocations(locations) }
    }

    private fun updateLocationsFromNetwork() {
        mAppExecutors.networkIO().execute {
            try {
                val response = mWebservice.getLocations().execute()
                if (response.isSuccessful) {
                    Log.d(TAG, "getLocations: " + response.body()!!.Location)
                    mLocationDao.insertAll(response.body()!!.Location)
                } else {
                    Log.e(TAG, "Locations request failed." + response.message())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        private val TAG = "MenuRepository"
    }


}
