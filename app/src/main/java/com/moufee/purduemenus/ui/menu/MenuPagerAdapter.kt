package com.moufee.purduemenus.ui.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.moufee.purduemenus.menus.DiningCourtMenu
import com.moufee.purduemenus.menus.Location
import java.util.ArrayList
import java.util.HashMap
import kotlin.collections.HashSet
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.Set

/**
 * Created by Ben on 26/09/2017.
 */

class MenuPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var diningCourtMap: Map<String, DiningCourtMenu> = HashMap()
    private var locationList: List<Location> = ArrayList()
    private var mealIndex: Int = 0
    private var favoritesSet: Set<String> = HashSet()
    private var showFavoriteCount = true

    fun setFavoritesSet(favoritesSet: Set<String>) {
        this.favoritesSet = favoritesSet
        notifyDataSetChanged()
    }

    fun setMenus(diningCourtMenus: Map<String, DiningCourtMenu>) {
        this.diningCourtMap = diningCourtMenus
        notifyDataSetChanged()
    }

    fun setLocationList(locationList: List<Location>) {
        this.locationList = locationList
        notifyDataSetChanged()
    }

    fun setMealIndex(mealIndex: Int) {
        this.mealIndex = mealIndex
        notifyDataSetChanged()
    }

    fun setShowFavoriteCount(showFavoriteCount: Boolean) {
        this.showFavoriteCount = showFavoriteCount
        notifyDataSetChanged()
    }

    private fun getLocationForIndex(index: Int): String {
        return locationList[index].Name
    }

    override fun getItem(position: Int): Fragment {
        val name = getLocationForIndex(position)
        return MenuItemListFragment.newInstance(name, mealIndex)
    }

    override fun getCount(): Int {
        return if (diningCourtMap.isEmpty()) {
            0
        } else locationList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        /*  if (object instanceof MenuItemListFragment) {
            String locationName = ((MenuItemListFragment) object).mDiningCourtName;
            for (int i = 0; i < locationList.size(); i++) {
                Location location = locationList.get(i);
                if (location.getName().equals(locationName)) {
                    return i;
                }
            }
        }*/
        //todo: better way to determine reordering of dining courts
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = locationList[position].Name
        if (!showFavoriteCount || favoritesSet.isEmpty())
            return title

        var numFavorites = 0
        if (diningCourtMap[title]?.isServing(mealIndex) == true)
            numFavorites = diningCourtMap[title]?.getMeal(mealIndex)?.getNumFavorites(favoritesSet)
                    ?: 0

        title += " ($numFavorites)"
        return title
    }
}
