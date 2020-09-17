package com.moufee.purduemenus.repository.data.menus

import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * A DayMenu contains all the menus for all meals on a given day
 * @property meals a map of meal name to the meal
 */
data class DayMenu(val date: LocalDate, val meals: Map<String, Meal>) {
    val hasLateLunch = meals["Late Lunch"] != null
}

/**
 * A Meal (e.g Breakfast, Lunch, Late Lunch, Dinner) contains all the dining court menus for a single meal on a single day
 * @property locations a map of the location name to the menu for this meal
 */
data class Meal(val name: String, val locations: Map<String, DiningCourtMeal>) {
    val openLocations = locations.filterValues { it.stations.isNotEmpty() && it.status == "Open" }
}

/**
 * The menu for a single meal at a single dining court
 * @property diningCourtName the name of the dining court
 */
data class DiningCourtMeal(
        val diningCourtName: String,
        val status: String,
        val startTime: LocalTime?,
        val endTime: LocalTime?,
        val stations: List<Station>
)

enum class MealType(name: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    LATE_LUNCH("Late Lunch"),
    DINNER("Dinner");
}