package com.moufee.purduemenus.menus;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all the menus for all campus dining courts for one day
 */

public class FullDayMenu implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, DiningCourtMenu> mMenuMap = new HashMap<>();
    private DateTime mDate;
    private boolean mLateLunchServed;

    public FullDayMenu(List<DiningCourtMenu> diningCourtMenus, DateTime date) {
//        mMenus = diningCourtMenus;
        mDate = date;
        mLateLunchServed = hasLateLunch();
        for (DiningCourtMenu menu :
                diningCourtMenus) {
            mMenuMap.put(menu.getLocation(), menu);
        }
    }

    public Map<String, DiningCourtMenu> getMenuMap() {
        return mMenuMap;
    }

    public DiningCourtMenu getMenu(String location) {
        return mMenuMap.get(location);
    }

    private boolean hasLateLunch() {
        for (DiningCourtMenu menu :
                mMenuMap.values()) {
            if (menu.servesLateLunch()) {
                return true;
            }
        }
        return false;
    }

    public int getNumMenus() {
        return mMenuMap.size();
    }

    public DateTime getDate() {
        return mDate;
    }

    public boolean isLateLunchServed() {
        return mLateLunchServed;
    }

}
