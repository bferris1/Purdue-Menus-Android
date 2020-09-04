package com.moufee.purduemenus.repository.data.menus

import org.joda.time.LocalDate
import org.joda.time.LocalTime

data class DayMenu(val date: LocalDate, val meals: Map<String, Meal>) {
    val hasLateLunch = meals["Late Lunch"] != null
}

data class Meal(val name: String, val locations: Map<String, DiningCourtMeal>)

data class DiningCourtMeal(val diningCourtName: String, val startTime: LocalTime?, val endTime: LocalTime?, val stations: List<Station>)

enum class MealType(name: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    LATE_LUNCH("Late Lunch"),
    DINNER("Dinner");
}