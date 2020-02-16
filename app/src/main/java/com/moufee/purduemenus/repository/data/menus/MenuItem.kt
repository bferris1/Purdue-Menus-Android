package com.moufee.purduemenus.repository.data.menus

import androidx.annotation.Keep

/**
 * Created by Ben on 9/5/17.
 * Represent one item on the menu
 */
@Keep
data class MenuItem(val name: String, val isVegetarian: Boolean = false, val id: String)