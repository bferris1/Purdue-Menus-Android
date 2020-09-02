package com.moufee.purduemenus.menus;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

/**
 * Mirrors JSON structure from API to allow easy deserialization
 * Contains inner classes to represent nested objects
 * Gson Constructs objects from JSON using these classes
 */
@Keep
public class DiningCourtMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    private String Location;
    private String Date;
    private String Notes;
    private List<Meal> Meals;

    public String getLocation() {
        return Location != null ? Location : "NULL";
    }

    public String getDate() {
        return Date;
    }

    public String getNotes() {
        return Notes;
    }

    public List<Meal> getMeals() {
        return Meals;
    }

    /**
     * Gets the meal associated with the provided index
     * Index 3 is dinner, but some dining courts only have three Meals (up to index 2)
     *
     * @param mealIndex the index of the meal to get
     * @return the Meal associated with the provided index
     */
    public Meal getMeal(int mealIndex) {
        if (Meals != null && Meals.size() > 0) {
            for (Meal meal :
                    Meals) {
                if (meal.getOrder() == mealIndex)
                    return meal;
            }
        }

        return null;
    }

    public boolean servesLateLunch() {
        if (Meals != null)
            for (Meal meal :
                    Meals) {
                if (meal.getName().equals("Late Lunch") && meal.isOpen())
                    return true;
            }
        return false;
//        return Meals != null && Meals.size() == 4 && Meals.get(2).isOpen();
    }

    public boolean isServing(int mealIndex) {
        return Meals != null && getMeal(mealIndex) != null && getMeal(mealIndex).isOpen();
    }


}