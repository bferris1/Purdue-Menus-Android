package com.moufee.purduemenus.ui.menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moufee.purduemenus.menus.DiningCourtMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 26/09/2017.
 */

public class MenuPagerAdapter extends FragmentStatePagerAdapter {

    private List<DiningCourtMenu> diningCourtMenus = new ArrayList<>();
    private int mealIndex;

    public MenuPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setMenus(List<DiningCourtMenu> diningCourtMenus){
        this.diningCourtMenus = diningCourtMenus;
        notifyDataSetChanged();
    }
    public void setMealIndex(int mealIndex){
        this.mealIndex = mealIndex;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        if (diningCourtMenus.get(position).isServing(mealIndex))
            return MenuItemListFragment.newInstance(position, mealIndex);
        else return NotServingFragment.newInstance("Not Serving");
    }

    @Override
    public int getCount() {
        return diningCourtMenus.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return diningCourtMenus.get(position).getLocation();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
