package com.moufee.purduemenus.repository

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.EmptyCoroutineContext


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
        if (locations == null)
            return MutableLiveData()
        return liveData(EmptyCoroutineContext + Dispatchers.IO) {
            var fullMenu: FullDayMenu? = null
            try {
                fullMenu = menuCache.get(dateTime)
                if (fullMenu != null) {
                    emit(Resource.success(fullMenu))
                    Timber.d("getFullMenu: Read from file!")
                } else {
                    emit(Resource.loading<FullDayMenu>(null))
                }
            } catch (t: Throwable) {
                emit(Resource.loading<FullDayMenu>(null))
                Timber.e(t)
            }
            try {
                fullMenu = menuDownloader.getMenus(dateTime, locations)
                emit(Resource.success(fullMenu))
                menuCache.put(fullMenu)
            } catch (t: Throwable) {
                Timber.e(t)
                emit(Resource.error("Network Error", fullMenu))
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
        val locationsDefaultOrder = mapOf("Earhart" to 0, "Windsor" to 1, "Wiley" to 2, "Ford" to 3, "Hillenbrand" to 4)
        CoroutineScope(EmptyCoroutineContext + Dispatchers.IO).launch {
            try {
                val response = mWebservice.getLocations().execute()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    for (location in body.Location) {
                        location.displayOrder = locationsDefaultOrder[location.Name] ?: 10
                    }
                    Timber.d("getLocations: ${response.body()?.Location}")
                    mLocationDao.insertAll(body.Location)
                    //todo: remove superfluous local locations?
                } else {
                    Timber.e("Locations request failed. ${response.message()}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
