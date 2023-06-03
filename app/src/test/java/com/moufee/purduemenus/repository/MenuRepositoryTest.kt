package com.moufee.purduemenus.repository

import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.api.models.ApiMeal
import com.moufee.purduemenus.api.models.RemoteDiningCourtMenu
import com.moufee.purduemenus.api.models.RemoteHours
import com.moufee.purduemenus.api.models.RemoteMenuItem
import com.moufee.purduemenus.api.models.RemoteStation
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.Test

class MenuRepositoryTest {


    @Test fun testApiToMealMenu() {
        val station = RemoteStation("PB Road", listOf(RemoteMenuItem("132", "Some Food", true, emptyList())))
        val apiMenu = RemoteDiningCourtMenu(
                Location = "Ford",
                Date = LocalDate.now(),
                Notes = "",
                Meals = listOf(ApiMeal("123abc", "Breakfast", 1, "Open", "DiningCourt", RemoteHours(LocalTime.now(), LocalTime.now()), listOf(station)))
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