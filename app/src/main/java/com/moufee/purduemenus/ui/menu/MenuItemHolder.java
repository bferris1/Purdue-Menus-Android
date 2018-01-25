package com.moufee.purduemenus.ui.menu;

import android.support.v7.widget.RecyclerView;

import com.moufee.purduemenus.databinding.FragmentMenuitemBinding;
import com.moufee.purduemenus.menus.MenuItem;

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

    public void bind(MenuItem item) {
        menuitemBinding.setMenuItem(item);
        menuitemBinding.executePendingBindings();
    }

}
