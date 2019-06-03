package com.moufee.purduemenus.ui.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.moufee.purduemenus.menus.DiningCourtMenu;
import com.moufee.purduemenus.menus.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ben on 26/09/2017.
 */

public class MenuPagerAdapter extends FragmentStatePagerAdapter {

    private Map<String, DiningCourtMenu> diningCourtMap = new HashMap<>();
    private List<Location> mLocationList = new ArrayList<>();
    private List<DiningCourtMenu> mMenusList = new ArrayList<>();
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

    public void setMenus(Map<String, DiningCourtMenu> diningCourtMenus) {
        this.diningCourtMap = diningCourtMenus;
        notifyDataSetChanged();
    }

    public void setLocationList(List<Location> locationList) {
        mLocationList = locationList;
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

    private String getLocationForIndex(int index) {
        return mLocationList.get(index).getName();
    }

    @Override
    public Fragment getItem(int position) {
        String name = getLocationForIndex(position);
        return MenuItemListFragment.newInstance(name, mealIndex);
    }

    @Override
    public int getCount() {
        if (diningCourtMap == null || diningCourtMap.size() == 0) {
            return 0;
        }
        return mLocationList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
      /*  if (object instanceof MenuItemListFragment) {
            String locationName = ((MenuItemListFragment) object).mDiningCourtName;
            for (int i = 0; i < mLocationList.size(); i++) {
                Location location = mLocationList.get(i);
                if (location.getName().equals(locationName)) {
                    return i;
                }
            }
        }*/
        //todo: better way to determine reordering of dining courts
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = mLocationList.get(position).getName();
        if (!mShowFavoriteCount || mFavoritesSet == null || mFavoritesSet.isEmpty())
            return title;

        int numFavorites = 0;
        if (diningCourtMap.get(title).isServing(mealIndex))
            numFavorites = diningCourtMap.get(title).getMeal(mealIndex).getNumFavorites(mFavoritesSet);

        title += " (" + numFavorites + ")";
        return title;
    }
}
