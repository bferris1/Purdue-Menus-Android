package com.moufee.purduemenus.ui.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal

/**
 * Created by Ben on 26/09/2017.
 */

class MenuPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var diningCourtMeals: List<DiningCourtMeal> = emptyList()
    private var mealIndex: Int = 0
    private var favoritesSet: Set<String> = HashSet()
    private var showFavoriteCount = true

    fun setFavoritesSet(favoritesSet: Set<String>) {
        this.favoritesSet = favoritesSet
        notifyDataSetChanged()
    }

    fun setMenus(diningCourtMenus: List<DiningCourtMeal>) {
        this.diningCourtMeals = diningCourtMenus
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
        return diningCourtMeals[index].diningCourtName
    }

    override fun getItem(position: Int): Fragment {
        val name = getLocationForIndex(position)
        return MenuItemListFragment.newInstance(name, position)
    }

    override fun getCount(): Int = diningCourtMeals.size

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
        return diningCourtMeals[position].diningCourtName
    }
    /*var title = locationList[position].Name
    if (!showFavoriteCount || favoritesSet.isEmpty())
        return title

    var numFavorites = 0
    if (diningCourtMap[title]?.isServing(mealIndex) == true)
        numFavorites = diningCourtMap[title]?.getMeal(mealIndex)?.getNumFavorites(favoritesSet)
                ?: 0

    title += " ($numFavorites)"
    return title
}*/
}
