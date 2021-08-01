package com.moufee.purduemenus.ui.menu

import androidx.lifecycle.*
import com.moufee.purduemenus.preferences.AppPreferenceManager
import com.moufee.purduemenus.preferences.AppPreferences
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.repository.data.menus.DayMenu
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.repository.data.menus.Location
import com.moufee.purduemenus.repository.data.menus.MenuItem
import com.moufee.purduemenus.util.DateTimeHelper
import com.moufee.purduemenus.util.Resource
import com.moufee.purduemenus.util.combineLatest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import timber.log.Timber
import javax.inject.Inject

/**
 * A ViewModel representing all the dining menus for one day.
 */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val mMenuRepository: MenuRepository,
    private val preferenceManager: AppPreferenceManager,
    private val favoritesRepository: FavoritesRepository
) : ViewModel(), OnToggleFavoriteListener {

    private val mCurrentDate = MutableLiveData<LocalDate>()
    private val mSelectedMeal = MutableLiveData<String>()

    private val _favoriteSet = MutableStateFlow<Set<String>>(emptySet())
    val favoriteSet: StateFlow<Set<String>> = _favoriteSet
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
            if (menu is Resource.Success) {
                if (mealName == "Late Lunch" && menu.data.hasLateLunch.not()) {
                    setSelectedMeal("Dinner")
                }
                menu.data.meals[mealName]
            } else null
        }.let {
            Transformations.map(combineLatest(it, appPreferences)) { (meal, prefs) ->
                if (prefs.hideClosedDiningCourts) meal?.openLocations ?: emptyMap() else meal?.locations ?: emptyMap()
            }
        }
        sortedLocations = Transformations.map(combineLatest(selectedMenus, locations)) { (menus, locations) ->
            locations.mapNotNull { menus[it.Name] }
        }
        viewModelScope.launch {
            favoritesRepository.favoriteIDSet.collect {
                Timber.d(it.toString())
                _favoriteSet.value = it
            }
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

    override fun toggleFavorite(item: MenuItem): Boolean {
        viewModelScope.launch {
            if (favoriteSet.value.contains(item.id)) favoritesRepository.removeFavorite(item) else favoritesRepository.addFavorite(item)
        }
        return true
    }
}
