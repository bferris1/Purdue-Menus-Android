package com.moufee.purduemenus

import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.api.MenuDownloader
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.menus.Location
import com.moufee.purduemenus.repository.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

val station = ApiStation("PB Road", listOf(MenuItem("132", "Some Food", true, emptyList())))
val apiMenu = ApiDiningCourtMenu(
        Location = "Ford",
        Date = LocalDate.now(),
        Notes = "",
        Meals = listOf(ApiMeal("123abc", "Breakfast", 1, "DiningCourt", "Open", ApiHours(LocalTime.now(), LocalTime.now()), listOf(station)))
)


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)

class MenuDownloaderTest {

    @Mock
    private lateinit var webservice: Webservice

    private val dateString = "2019-05-29"
    private val testDate = LocalDate.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd"))

    @Before
    fun setup() = runBlockingTest {

        `when`(webservice.getMenu("Failure", dateString)).thenThrow(RuntimeException("Some Network Error Occurred"))
        `when`(webservice.getMenu("Success", dateString)).thenReturn(apiMenu)
    }

    @Test
    fun testSuccess() = runBlockingTest {
        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        val singleSuccess = downloader.getMenus(testDate, locations)
        verify(webservice, times(3)).getMenu("Success", dateString)
//        assertThat(singleSuccess.getMenu("Success")).isNotNull()
        assertThat(singleSuccess).hasSize(3)

    }

    @Test
    fun testFailure() = runBlockingTest {

        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        val singleSuccess = downloader.getMenus(testDate, locations)
        verify(webservice).getMenu("Success", dateString)
        verify(webservice).getMenu("Failure", dateString)
//        assertThat(singleSuccess.getMenu("Success")).isNotNull()
//        assertThat(singleSuccess.getMenu("Failure")).isNull()
        assertThat(singleSuccess).hasSize(1)
    }

    @Test
    fun testAllFailure() = runBlockingTest {

        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        val singleFailure = downloader.getMenus(testDate, locations)
        verify(webservice, times(3)).getMenu("Failure", dateString)
//        assertThat(singleFailure.getMenu("Failure")).isNull()
        assertThat(singleFailure).hasSize(0)
    }
}