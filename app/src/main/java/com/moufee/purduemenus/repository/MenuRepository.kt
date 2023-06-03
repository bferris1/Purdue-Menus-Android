package com.moufee.purduemenus.repository

import androidx.lifecycle.LiveData
import com.moufee.purduemenus.api.MenuCache
import com.moufee.purduemenus.api.MenuDownloader
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.api.models.RemoteDiningCourtMenu
import com.moufee.purduemenus.api.models.RemoteLocation
import com.moufee.purduemenus.api.models.RemoteMenuItem
import com.moufee.purduemenus.api.models.RemoteStation
import com.moufee.purduemenus.db.LocationDao
import com.moufee.purduemenus.repository.data.menus.DayMenu
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.repository.data.menus.Location
import com.moufee.purduemenus.repository.data.menus.Meal
import com.moufee.purduemenus.repository.data.menus.MenuItem
import com.moufee.purduemenus.repository.data.menus.Station
import com.moufee.purduemenus.util.AppExecutors
import com.moufee.purduemenus.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
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
constructor(private val mWebservice: Webservice,
            private val mAppExecutors: AppExecutors,
            private val mLocationDao: LocationDao,
            private val menuDownloader: MenuDownloader,
            private val menuCache: MenuCache) {

    val locations: LiveData<List<Location>>
        get() {
            updateLocationsFromNetwork()
            return mLocationDao.getAll()
        }

    // the first time this is called when the app is first installed (has no data) it will emit an empty list
    val visibleLocations: Flow<List<Location>>
        get() {
            updateLocationsFromNetwork()
            return mLocationDao.getVisible()
        }

    fun observeVisibleMenus(dateTime: LocalDate) = mLocationDao.getVisible().flatMapLatest {
        observeMenus(dateTime, it)
    }


    private fun observeMenus(dateTime: LocalDate, locations: List<Location>?): Flow<Resource<DayMenu>> = flow {
        if (locations == null) return@flow
        var fullMenu: DayMenu? = null
        try {
            fullMenu = menuCache.get(dateTime)
            if (fullMenu != null) {
                emit(Resource.Success(fullMenu))
                Timber.d("getFullMenu: Read from file!")
            } else {
                Timber.d("Could not read from file")
                emit(Resource.Loading)
            }
        } catch (t: Throwable) {
            emit(Resource.Loading)
            Timber.e(t)
        }
        try {
            fullMenu = menuDownloader.getMenus(dateTime, locations).toDayMenu(dateTime)
            emit(Resource.Success(fullMenu))
            menuCache.put(fullMenu)
        } catch (t: Throwable) {
            Timber.e(t)
            if (fullMenu == null)
                emit(Resource.Error(t as Exception))
        }
    }.flowOn(Dispatchers.IO)

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
                val response = mWebservice.getLocations()
                val locations = response.body()?.Location?.map { it.toLocation() }
                if (response.isSuccessful && locations != null) {
                    for (location in locations) {
                        location.displayOrder = locationsDefaultOrder[location.Name] ?: 100
                    }
                    Timber.d("getLocations: ${response.body()?.Location}")
                    mLocationDao.insertAll(locations)
                    val currentLocations = mLocationDao.getAllList()
                    currentLocations.forEach { current -> if (locations.find { it.LocationId == current.LocationId } == null) mLocationDao.delete(current) }
                } else {
                    Timber.e("Locations request failed. ${response.message()}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}

fun List<RemoteDiningCourtMenu>.toDayMenu(dateTime: LocalDate): DayMenu {
    val mealNameToMealListMap: MutableMap<String, MutableList<DiningCourtMeal>> = HashMap()
    for (apiDiningCourtMenu in this) {
        for (apiMeal in apiDiningCourtMenu.Meals) {
            if (mealNameToMealListMap.containsKey(apiMeal.Name).not())
                mealNameToMealListMap[apiMeal.Name] = mutableListOf()
            mealNameToMealListMap[apiMeal.Name]?.add(DiningCourtMeal(
                    diningCourtName = apiDiningCourtMenu.Location,
                    status = apiMeal.Status,
                    startTime = apiMeal.Hours?.StartTime,
                    endTime = apiMeal.Hours?.EndTime,
                    stations = apiMeal.Stations.toEntity()
            ))
        }
    }
    return DayMenu(dateTime, mealNameToMealListMap.mapValues { (name, mealList) -> Meal(name, mealList.map { Pair(it.diningCourtName, it) }.toMap()) })
}

fun List<RemoteStation>.toEntity() = map { Station(it.Name, it.Items.map { item -> item.toEntity() }) }

fun RemoteMenuItem.toEntity() = MenuItem(Name, IsVegetarian, ID)

fun RemoteLocation.toLocation() = Location(this.Name, this.LocationId, this.FormalName)
