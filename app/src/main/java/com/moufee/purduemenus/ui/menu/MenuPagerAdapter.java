package com.moufee.purduemenus.ui.menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moufee.purduemenus.menus.DiningCourtMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Ben on 26/09/2017.
 */

public class MenuPagerAdapter extends FragmentStatePagerAdapter {

    private List<DiningCourtMenu> diningCourtMenus = new ArrayList<>();
    private int mealIndex;
    private Set<String> mFavoritesSet;
    private boolean mShowFavoriteCount = true;

    public void setFavoritesSet(Set<String> favoritesSet) {
        mFavoritesSet = favoritesSet;
        notifyDataSetChanged();
    }

    public MenuPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setMenus(List<DiningCourtMenu> diningCourtMenus) {
        this.diningCourtMenus = diningCourtMenus;
        notifyDataSetChanged();
    }

    public void setMealIndex(int mealIndex) {
        this.mealIndex = mealIndex;
        notifyDataSetChanged();
    }

    public void setShowFavoriteCount(boolean showFavoriteCount) {
        mShowFavoriteCount = showFavoriteCount;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return MenuItemListFragment.newInstance(position, mealIndex);
    }

    @Override
    public int getCount() {
        return diningCourtMenus.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = diningCourtMenus.get(position).getLocation();
        if (!mShowFavoriteCount || mFavoritesSet == null || mFavoritesSet.isEmpty())
            return title;

        int numFavorites = 0;
        if (diningCourtMenus.get(position).isServing(mealIndex))
            numFavorites = diningCourtMenus.get(position).getMeal(mealIndex).getNumFavorites(mFavoritesSet);

        title += " (" + numFavorites + ")";
        return title;
    }
}
