package com.moufee.purduemenus.ui.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.repository.data.menus.MenuItem
import com.moufee.purduemenus.repository.data.menus.Station

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

        val diningCourtName = diningCourtMeals[position].diningCourtName
        if (!showFavoriteCount || favoritesSet.isEmpty())
            return diningCourtName

        val favoriteCount = diningCourtMeals.getOrNull(position)?.stations?.countFavorites(favoritesSet) ?: 0

        return "$diningCourtName ($favoriteCount)"
    }

    private fun List<Station>.countFavorites(favoriteItemIds: Set<String>): Int {
        val favorites: MutableSet<MenuItem> = HashSet()
        forEach { station ->
            station.items.forEach {
                if (it.id in favoriteItemIds) favorites.add(it)
            }
        }
        return favorites.size
    }
}
