package com.moufee.purduemenus.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.databinding.FragmentMenuitemBinding;
import com.moufee.purduemenus.menus.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ben on 14/07/2017.
 * RecyclerView ViewHolder for a menu item
 *
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
