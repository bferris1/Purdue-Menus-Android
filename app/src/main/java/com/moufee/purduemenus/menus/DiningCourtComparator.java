package com.moufee.purduemenus.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sorts Dining Courts into a fixed order based on their name
 */

public class DiningCourtComparator implements Comparator<DiningCourtMenu> {
    public static final List<String> diningCourts = new ArrayList<>(Arrays.asList("Earhart", "Ford", "Wiley", "Windsor", "Hillenbrand"));

    private List<String> sortOrder;

    private List<Location> mLocationOrder;
    private Map<String, Integer> mSortOrderMap = new HashMap<>();

    public DiningCourtComparator() {
        sortOrder = diningCourts;
    }

    public DiningCourtComparator(List<Location> sortOrder) {
        this.mLocationOrder = sortOrder;
        for (Location location :
                mLocationOrder) {
            mSortOrderMap.put(location.getName(), location.getDisplayOrder());
        }
    }


    @Override
    public int compare(DiningCourtMenu o1, DiningCourtMenu o2) {
        if (o1 == null || o2 == null)
            return 0;
        Integer index1 = mSortOrderMap.get(o1.getLocation());
        Integer index2 = mSortOrderMap.get(o2.getLocation());
        if (index1 == null || index2 == null) return 0;
        if (index1 < index2)
            return -1;
        if (index1 > index2)
            return 1;
        return 0;
    }
}
