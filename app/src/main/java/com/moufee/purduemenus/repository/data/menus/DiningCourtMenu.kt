package com.moufee.purduemenus.repository.data.menus

import androidx.annotation.Keep


@Keep
data class DiningCourtMenu(val diningCourtName: String, val stations: List<Station>) {


    /**
     * Gets the meal associated with the provided index
     * Index 3 is dinner, but some dining courts only have three Meals (up to index 2)
     *
     * @param mealIndex the index of the meal to get
     * @return the OldMeal associated with the provided index
     */
    /*fun getMeal(mealIndex: Int): OldMeal? {
        if (meals.isNotEmpty()) {
            for (meal in meals) {
                if (meal.order == mealIndex + 1) return meal
            }
        }
        return null
    }

    fun isServing(mealIndex: Int): Boolean {
        return getMeal(mealIndex)?.isOpen == true
    }*/
}