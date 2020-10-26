package com.moufee.purduemenus.ui.menu

import androidx.recyclerview.widget.RecyclerView
import com.moufee.purduemenus.databinding.FragmentMenuitemBinding
import com.moufee.purduemenus.repository.data.menus.MenuItem

/**
 * Created by Ben on 14/07/2017.
 * RecyclerView ViewHolder for a menu item
 */
class MenuItemHolder(private val menuItemBinding: FragmentMenuitemBinding) : RecyclerView.ViewHolder(menuItemBinding.root) {
    fun bind(item: MenuItem?, listener: OnToggleFavoriteListener?, isFavorite: Boolean) {
        menuItemBinding.menuItem = item
        menuItemBinding.isFavorite = isFavorite
        menuItemBinding.listener = listener
        menuItemBinding.executePendingBindings()
    }
}