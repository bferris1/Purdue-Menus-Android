package com.moufee.purduemenus.ui.menu

import com.moufee.purduemenus.repository.data.menus.MenuItem

sealed class MenuListViewObject

data class MenuItemViewObject(val menuItem: MenuItem, val id: String, val name: String, val isVegetarian: Boolean, val isFavorite: Boolean) : MenuListViewObject()

data class HeaderItemViewObject(val stationName: String) : MenuListViewObject()
