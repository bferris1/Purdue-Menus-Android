package com.moufee.purduemenus.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moufee.purduemenus.AppPreferences
import com.moufee.purduemenus.preferences.AppPreferenceManager
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.repository.data.menus.DayMenu
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.repository.data.menus.Location
import com.moufee.purduemenus.repository.data.menus.MenuItem
import com.moufee.purduemenus.util.DateTimeHelper
import com.moufee.purduemenus.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

/**
 * A ViewModel representing all the dining menus for one day.
 */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val mMenuRepository: MenuRepository,
    private val preferenceManager: AppPreferenceManager,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val timeFormatter: DateTimeFormatter = DateTimeFormat.shortTime()

    private val mCurrentDate = MutableStateFlow<LocalDate>(LocalDate.now())
    private val mSelectedMeal = MutableStateFlow<String?>(null)

    val favoriteSet: StateFlow<Set<String>> = favoritesRepository.favoriteIDSet.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )
    val locations: StateFlow<List<Location>> = mMenuRepository.visibleLocations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val dayMenu: StateFlow<Resource<DayMenu>>
    val selectedMenus: StateFlow<Map<String, DiningCourtMeal>> // menus for selected meal, location name to meal menu
    val sortedLocations: StateFlow<List<DiningCourtMeal>>
    val appPreferences: StateFlow<AppPreferences> by lazy {
        preferenceManager.preferencesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppPreferences.getDefaultInstance()
        )
    }

    val selectedMeal: StateFlow<String?>
        get() = mSelectedMeal.asStateFlow()


    val currentDate: StateFlow<LocalDate>
        get() = mCurrentDate.asStateFlow()

    init {
        mSelectedMeal.value = DateTimeHelper.getCurrentMeal()
        dayMenu = mCurrentDate.flatMapLatest {
            mMenuRepository.observeVisibleMenus(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )
        val currentMeal = combine(dayMenu, selectedMeal) { menu, mealName ->
            if (menu is Resource.Success<DayMenu>) {
                if (mealName == "Late Lunch" && menu.data.hasLateLunch.not()) {
                    setSelectedMeal("Dinner")
                }
                menu.data.meals[mealName]
            } else null
        }
        selectedMenus = combine(currentMeal, preferenceManager.preferencesFlow) { meal, prefs ->
            if (prefs.hideClosedDiningCourts) meal?.openLocations ?: emptyMap() else meal?.locations ?: emptyMap()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
        sortedLocations = combine(selectedMenus, locations) { menus, locations ->
            locations.mapNotNull { menus[it.Name] }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            dayMenu.collect {
                Timber.d(it.toString())
            }
        }
    }

    fun getMenuDetailsUiState(locationName: String) = selectedMenus.combine(favoriteSet, ::Pair).map { (meals, favoriteIds) ->
        val menu = meals[locationName]
        val stations = menu?.stations ?: emptyList()
        val items = stations.flatMap { station ->
            listOf(HeaderItemViewObject(station.name)).plus(station.items.map {
                MenuItemViewObject(
                    menuItem = it,
                    id = it.id,
                    name = it.name,
                    isVegetarian = it.isVegetarian,
                    isFavorite = it.id in favoriteIds
                )
            })
        }
        val servingTimeText = if (menu?.startTime != null && menu.endTime != null) {
            "${timeFormatter.print(menu.startTime)} - ${timeFormatter.print(menu.endTime)}"
        } else {
            ""
        }
        MealDetailUiState(items, servingTimeText, menu?.status)
    }

    data class MealDetailUiState(val items: List<MenuListViewObject>, val servingTimeText: String, val status: String?)


    fun setSelectedMeal(meal: String) {
        mSelectedMeal.value = meal
    }

    fun setDate(date: LocalDate) {
        mCurrentDate.value = date
    }

    fun nextDay() {
        mCurrentDate.value = mCurrentDate.value.plusDays(1)
    }

    fun previousDay() {
        mCurrentDate.value = mCurrentDate.value.plusDays(-1)
    }

    fun currentDay() {
        mCurrentDate.value = LocalDate.now()
    }

    fun reloadData() {
        mCurrentDate.value.let { mCurrentDate.value = it }
    }

    fun toggleFavorite(item: MenuItem): Boolean {
        viewModelScope.launch {
            if (favoriteSet.value.contains(item.id)) favoritesRepository.removeFavorite(item) else favoritesRepository.addFavorite(item)
        }
        return true
    }
}
