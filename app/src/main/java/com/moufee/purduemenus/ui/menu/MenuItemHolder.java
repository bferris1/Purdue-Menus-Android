package com.moufee.purduemenus.ui.menu;

import com.moufee.purduemenus.databinding.FragmentMenuitemBinding;
import com.moufee.purduemenus.menus.MenuItem;
import com.moufee.purduemenus.menus.OnToggleFavoriteListener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Ben on 14/07/2017.
 * RecyclerView ViewHolder for a menu item
 */

public class MenuItemHolder extends RecyclerView.ViewHolder {
    public FragmentMenuitemBinding menuitemBinding;


    public MenuItemHolder(FragmentMenuitemBinding binding) {
        super(binding.getRoot());
        menuitemBinding = binding;
    }

    public void bind(MenuItem item, OnToggleFavoriteListener listener, boolean isFavorite) {
        menuitemBinding.setMenuItem(item);
        menuitemBinding.setIsFavorite(isFavorite);
        menuitemBinding.setListener(listener);
        menuitemBinding.executePendingBindings();
    }

}
