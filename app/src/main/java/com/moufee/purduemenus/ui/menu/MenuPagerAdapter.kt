package com.moufee.purduemenus.ui.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal

/**
 * Created by Ben on 26/09/2017.
 */

class MenuPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private var diningCourtMeals: List<DiningCourtMeal> = emptyList()
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

    fun setShowFavoriteCount(showFavoriteCount: Boolean) {
        this.showFavoriteCount = showFavoriteCount
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return diningCourtMeals[position].diningCourtName.hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return diningCourtMeals.find { it.diningCourtName.hashCode().toLong() == itemId } != null
    }

    override fun createFragment(position: Int): Fragment {
        val name = diningCourtMeals[position].diningCourtName
        return MenuItemListFragment.newInstance(name)
    }

    override fun getItemCount(): Int = diningCourtMeals.size

    fun getPageTitle(position: Int): CharSequence? {
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
