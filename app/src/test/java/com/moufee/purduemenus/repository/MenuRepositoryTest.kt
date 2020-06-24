package com.moufee.purduemenus.repository

import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.api.models.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.Test

class MenuRepositoryTest {


    @Test
    fun testApiToMealMenu() {
        val station = ApiStation("PB Road", listOf(MenuItem("132", "Some Food", true, emptyList())))
        val apiMenu = ApiDiningCourtMenu(
                Location = "Ford",
                Date = LocalDate.now(),
                Notes = "",
                Meals = listOf(ApiMeal("123abc", "Breakfast", 1, "Open", "DiningCourt", ApiHours(LocalTime.now(), LocalTime.now()), listOf(station)))
        )
        val result = listOf(apiMenu).toDayMenu(LocalDate.now())
        assertThat(result.meals).hasSize(1)
        assertThat(result.meals.getOrNull(0)?.locations).hasSize(1)
        assertThat(result.meals[0].name).isEqualTo("Breakfast")
        assertThat(result.meals[0].locations).hasSize(1)
        assertThat(result.meals[0].locations["Ford"]).isNotNull()
        assertThat(result.meals[0].locations["Ford"]?.stations).hasSize(1)
        assertThat(result.meals[0].locations["Ford"]?.stations?.get(0)?.items?.get(0)?.name).isEqualTo("Some Food")
    }
}