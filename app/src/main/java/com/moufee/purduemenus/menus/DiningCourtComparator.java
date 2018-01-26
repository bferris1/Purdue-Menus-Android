package com.moufee.purduemenus.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts Dining Courts into a fixed order based on their name
 */

public class DiningCourtComparator implements Comparator<DiningCourtMenu> {
    private static final List<String> diningCourts = new ArrayList<>(Arrays.asList("earhart", "ford", "wiley", "windsor", "hillenbrand", "the gathering place"));

    @Override
    public int compare(DiningCourtMenu o1, DiningCourtMenu o2) {
        if (o1 == null || o2 == null)
            return 0;
        int index1 = diningCourts.indexOf(o1.getLocation().toLowerCase());
        int index2 = diningCourts.indexOf(o2.getLocation().toLowerCase());

        if (index1 < index2)
            return -1;
        if (index1 > index2)
            return 1;
        return 0;
    }
}
