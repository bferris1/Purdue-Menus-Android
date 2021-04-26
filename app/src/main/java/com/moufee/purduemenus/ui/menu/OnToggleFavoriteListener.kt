package com.moufee.purduemenus.ui.menu

import com.moufee.purduemenus.repository.data.menus.MenuItem

/**
 * Created by Ben on 3/5/18.
 * Used by the recyclerView to notify a fragment or activity
 * that favorite state should be toggled
 */
interface OnToggleFavoriteListener {
    fun toggleFavorite(item: MenuItem): Boolean
}