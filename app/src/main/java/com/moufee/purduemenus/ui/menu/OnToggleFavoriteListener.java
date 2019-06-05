package com.moufee.purduemenus.ui.menu;

import com.moufee.purduemenus.menus.MenuItem;

/**
 * Created by Ben on 3/5/18.
 * Used by the recyclerView to notify a fragment or activity
 * that favorite state should be toggled
 */

public interface OnToggleFavoriteListener {
    boolean toggleFavorite(MenuItem item);
}
