package com.moufee.purduemenus.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.menus.DiningCourtMenu;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ben on 14/07/2017.
 * RecyclerView ViewHolder for a menu item
 * TextView for item name, image for vegetarian status, and button for favorite status
 * todo: access modifiers for fields?
 */

public class MenuItemHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.menu_item_name)  TextView mItemNameTextView;
    public final View mView;
    @BindView(R.id.imageview_vegetarian_icon) ImageView mVegetarianIcon;
    @BindView(R.id.favorite_button) ImageButton mFavoriteButton;
    public DiningCourtMenu.MenuItem mItem;


    public MenuItemHolder(View view) {
        super(view);
        mView = view;
        ButterKnife.bind(this, view);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mItemNameTextView.getText() + "'";
    }
}
