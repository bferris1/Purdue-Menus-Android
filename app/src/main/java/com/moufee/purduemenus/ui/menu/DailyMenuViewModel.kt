package com.moufee.purduemenus.ui.menu

import androidx.lifecycle.*
import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.util.DateTimeHelper
import com.moufee.purduemenus.util.Resource
import org.joda.time.DateTime
import javax.inject.Inject


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
    private val mCurrentDate = MutableLiveData<DateTime>()
    private val mSelectedMealIndex = MutableLiveData<Int>()

    val favoriteSet: LiveData<Set<String>> = mFavoritesRepository.favoriteIDSet
    private val locations: LiveData<List<Location>>
    private val mFullMenu: LiveData<Resource<FullDayMenu>>

    private val updatedMenu = MediatorLiveData<Resource<FullDayMenu>>()

    val selectedMealIndex: LiveData<Int>
        get() = mSelectedMealIndex


    val fullMenu: LiveData<Resource<FullDayMenu>>
        get() = mFullMenu

    val currentDate: LiveData<DateTime>
        get() = mCurrentDate

    init {
        mSelectedMealIndex.value = DateTimeHelper.getCurrentMealIndex()
        locations = mMenuRepository.locations
        setDate(DateTime())
        mFullMenu = Transformations.switchMap(combineLatest(mCurrentDate, locations)) { mMenuRepository.getMenus(it.first, it.second) }


    }

    fun setSelectedMealIndex(index: Int) {
        mSelectedMealIndex.value = index
    }

    fun setDate(date: DateTime) {
        mCurrentDate.value = date
    }

    fun nextDay() {
        if (mCurrentDate.value != null)
            mCurrentDate.value = mCurrentDate.value!!.plusDays(1)
    }

    fun previousDay() {
        if (mCurrentDate.value != null)
            mCurrentDate.value = mCurrentDate.value!!.plusDays(-1)
    }

    fun currentDay() {
        mCurrentDate.value = DateTime()
    }
}
