package com.moufee.purduemenus.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.moufee.purduemenus.preferences.AppPreferenceManager
import com.moufee.purduemenus.preferences.AppPreferences
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.repository.data.menus.DayMenu
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.repository.data.menus.Location
import com.moufee.purduemenus.util.DateTimeHelper
import com.moufee.purduemenus.util.Resource
import com.moufee.purduemenus.util.combineLatest
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * A ViewModel representing all the dining menus for one day.
 */
class DailyMenuViewModel @Inject constructor(private val mMenuRepository: MenuRepository,
                                             private val preferenceManager: AppPreferenceManager,
                                             mFavoritesRepository: FavoritesRepository) : ViewModel() {

    private val mCurrentDate = MutableLiveData<LocalDate>()
    private val mSelectedMeal = MutableLiveData<String>()

    val favoriteSet: LiveData<Set<String>> = mFavoritesRepository.favoriteIDSet
    val locations: LiveData<List<Location>>
    val dayMenu: LiveData<Resource<DayMenu>>
    val selectedMenus: LiveData<Map<String, DiningCourtMeal>>
    val sortedLocations: LiveData<List<DiningCourtMeal>>
    val appPreferences: LiveData<AppPreferences> by lazy { preferenceManager.preferences }

    val selectedMeal: LiveData<String>
        get() = mSelectedMeal


    val currentDate: LiveData<LocalDate>
        get() = mCurrentDate

    init {
        mSelectedMeal.value = DateTimeHelper.getCurrentMeal()
        locations = mMenuRepository.visibleLocations
        setDate(LocalDate.now())
        dayMenu = Transformations.switchMap(combineLatest(mCurrentDate, locations)) { (date, locations) ->
            if (locations.isEmpty()) return@switchMap null
            mMenuRepository.getMenus(date, locations)
        }
        selectedMenus = Transformations.map(combineLatest(dayMenu, selectedMeal)) { (menu, mealName) ->
            if (mealName == "Late Lunch" && menu.data?.hasLateLunch == false) {
                setSelectedMeal("Dinner")
            }
            menu.data?.meals?.get(mealName)
        }.let {
            Transformations.map(combineLatest(it, appPreferences)) { (meal, prefs) ->
                if (prefs.hideClosedDiningCourts) meal?.openLocations ?: emptyMap() else meal?.locations ?: emptyMap()
            }
        }
        sortedLocations = Transformations.map(combineLatest(selectedMenus, locations)) { (menus, locations) ->
            locations.mapNotNull { menus[it.Name] }
        }
    }


    fun setSelectedMeal(meal: String) {
        mSelectedMeal.value = meal
    }

    fun setDate(date: LocalDate) {
        mCurrentDate.value = date
    }

    fun nextDay() {
        mCurrentDate.value = mCurrentDate.value?.plusDays(1)
    }

    fun previousDay() {
        mCurrentDate.value = mCurrentDate.value?.plusDays(-1)
    }

    fun currentDay() {
        mCurrentDate.value = LocalDate.now()
    }

    fun reloadData() {
        mCurrentDate.value?.let { mCurrentDate.postValue(it) }
    }
}
