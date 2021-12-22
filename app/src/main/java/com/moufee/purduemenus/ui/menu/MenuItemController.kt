package com.moufee.purduemenus.ui.menu

import com.airbnb.epoxy.TypedEpoxyController
import com.moufee.purduemenus.menuItem
import com.moufee.purduemenus.repository.data.menus.MenuItem
import com.moufee.purduemenus.stationHeader

class MenuItemController(private val callbacks: AdapterCallbacks) : TypedEpoxyController<List<MenuListViewObject>>() {

    override fun buildModels(data: List<MenuListViewObject>) {
        data.forEach {
            when (it) {
                is HeaderItemViewObject -> stationHeader {
                    id(it.stationName)
                    stationName(it.stationName)
                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                }
                is MenuItemViewObject -> menuItem {
                    id(it.id)
                    menuItem(it)
                    listener(this@MenuItemController.callbacks)
                }
            }
        }

    }

    interface AdapterCallbacks {
        fun onItemLongPressed(item: MenuItemViewObject): Boolean
    }
}

sealed class MenuListViewObject

data class MenuItemViewObject(val menuItem: MenuItem, val id: String, val name: String, val isVegetarian: Boolean, val isFavorite: Boolean) : MenuListViewObject()

data class HeaderItemViewObject(val stationName: String) : MenuListViewObject()