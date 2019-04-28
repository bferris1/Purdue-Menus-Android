package com.moufee.purduemenus.menus

import java.util.*

/**
 * Sorts Dining Courts into a fixed order based on their name
 */

class DiningCourtComparator(mLocationOrder: List<Location>) : Comparator<DiningCourtMenu> {

    private val mSortOrderMap = HashMap<String, Int>()

    init {
        for ((Name, _, _, displayOrder) in mLocationOrder) {
            mSortOrderMap[Name] = displayOrder
        }
    }


    override fun compare(o1: DiningCourtMenu?, o2: DiningCourtMenu?): Int {
        if (o1 == null || o2 == null)
            return 0
        val index1 = mSortOrderMap[o1.location]
        val index2 = mSortOrderMap[o2.location]
        if (index1 == null || index2 == null) return 0
        if (index1 < index2)
            return -1
        return if (index1 > index2) 1 else 0
    }

    companion object {
        val diningCourts: List<String> = ArrayList(Arrays.asList("Earhart", "Ford", "Wiley", "Windsor", "Hillenbrand"))
    }
}
