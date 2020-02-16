package com.moufee.purduemenus.ui.menu

import androidx.lifecycle.*
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.repository.data.menus.DayMenu
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.repository.data.menus.Location
import com.moufee.purduemenus.util.DateTimeHelper
import com.moufee.purduemenus.util.Resource
import org.joda.time.LocalDate
import javax.inject.Inject

// todo: move this somewhere else?
// this is built-in functionality with RxJava, but not architecture components
fun <A, B> combineLatest(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

/**
 * A ViewModel representing all the dining menus for one day.
 */
class DailyMenuViewModel @Inject
constructor(private val mMenuRepository: MenuRepository, mFavoritesRepository: FavoritesRepository) : ViewModel() {
    private val mCurrentDate = MutableLiveData<LocalDate>()
    private val mSelectedMealIndex = MutableLiveData<Int>()

    val favoriteSet: LiveData<Set<String>> = mFavoritesRepository.favoriteIDSet
    val locations: LiveData<List<Location>>
    val dayMenu: LiveData<Resource<DayMenu>>
    val selectedMenus: LiveData<Map<String, DiningCourtMeal>>
    val sortedLocations: LiveData<List<DiningCourtMeal>>

    val selectedMealIndex: LiveData<Int>
        get() = mSelectedMealIndex


    val currentDate: LiveData<LocalDate>
        get() = mCurrentDate

    init {
        mSelectedMealIndex.value = DateTimeHelper.getCurrentMealIndex()
        locations = mMenuRepository.visibleLocations
        setDate(LocalDate.now())
        dayMenu = Transformations.switchMap(combineLatest(mCurrentDate, locations)) { (date, locations) ->
            if (locations.isEmpty()) return@switchMap null
            mMenuRepository.getMenus(date, locations)
        }
        selectedMenus = Transformations.map(combineLatest(dayMenu, selectedMealIndex)) { (menu, mealIndex) ->
            menu.data?.meals?.getOrNull(mealIndex)?.locations ?: emptyMap()
        }
        sortedLocations = Transformations.map(combineLatest(selectedMenus, locations)) { (menus, locations) ->
            locations.mapNotNull { menus[it.Name] }
        }
    }


    fun setSelectedMealIndex(index: Int) {
        mSelectedMealIndex.value = index
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
}
