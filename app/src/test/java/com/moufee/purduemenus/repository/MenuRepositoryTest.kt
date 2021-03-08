package com.moufee.purduemenus.repository

import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.api.models.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.Test

class MenuRepositoryTest {


    @Test fun testApiToMealMenu() {
        val station = ApiStation("PB Road", listOf(ApiMenuItem("132", "Some Food", true, emptyList())))
        val apiMenu = ApiDiningCourtMenu(
                Location = "Ford",
                Date = LocalDate.now(),
                Notes = "",
                Meals = listOf(ApiMeal("123abc", "Breakfast", 1, "Open", "DiningCourt", ApiHours(LocalTime.now(), LocalTime.now()), listOf(station)))
        )

        val result = listOf(apiMenu).toDayMenu(LocalDate.now())

        assertThat(result.meals).hasSize(1)
        assertThat(result.meals["Breakfast"]).isNotNull()
        assertThat(result.meals["Breakfast"]?.locations).hasSize(1)
        result.meals.getValue("Breakfast").apply {
            assertThat(name).isEqualTo("Breakfast")
            assertThat(locations).hasSize(1)
            assertThat(locations["Ford"]).isNotNull()
            assertThat(locations.getValue("Ford").stations).hasSize(1)
            assertThat(locations.getValue("Ford").stations[0].items[0].name).isEqualTo("Some Food")
        }
    }
}