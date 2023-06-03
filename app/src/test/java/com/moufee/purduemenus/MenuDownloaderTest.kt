package com.moufee.purduemenus

import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.api.MenuDownloader
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.api.models.ApiMeal
import com.moufee.purduemenus.api.models.RemoteDiningCourtMenu
import com.moufee.purduemenus.api.models.RemoteHours
import com.moufee.purduemenus.api.models.RemoteMenuItem
import com.moufee.purduemenus.api.models.RemoteStation
import com.moufee.purduemenus.repository.data.menus.Location
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test

val station = RemoteStation("PB Road", listOf(RemoteMenuItem("132", "Some Food", true, emptyList())))
val apiMenu = RemoteDiningCourtMenu(
        Location = "Ford",
        Date = LocalDate.now(),
        Notes = "",
        Meals = listOf(ApiMeal("123abc", "Breakfast", 1, "DiningCourt", "Open", RemoteHours(LocalTime.now(), LocalTime.now()), listOf(station)))
)

@ExperimentalCoroutinesApi
class MenuDownloaderTest {
    @MockK lateinit var webservice: Webservice
    private val dateString = "2019-05-29"
    private val testDate = LocalDate.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd"))

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        coEvery { webservice.getMenu("Failure", dateString) } throws RuntimeException("Some Network Error Occurred")
        coEvery { webservice.getMenu("Success", dateString) } returns apiMenu
    }

    @Test
    fun testSuccess() = runBlockingTest {
        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        val singleSuccess = downloader.getMenus(testDate, locations)
        coVerify(exactly = 3) { (webservice).getMenu("Success", dateString) }
        assertThat(singleSuccess).hasSize(3)
    }

    @Test
    fun testFailure() = runBlockingTest {
        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        val singleSuccess = downloader.getMenus(testDate, locations)
        coVerify { webservice.getMenu("Success", dateString) }
        coVerify { webservice.getMenu("Failure", dateString) }
        assertThat(singleSuccess).hasSize(1)
    }

    @Test
    fun testAllFailure() = runBlockingTest {
        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        var exceptionThrown = false
        try {
            downloader.getMenus(testDate, locations)
        } catch (e: RuntimeException) {
            exceptionThrown = true
        } finally {
            coVerify(exactly = 3) { webservice.getMenu("Failure", dateString) }
            assertThat(exceptionThrown).isTrue()
        }
    }
}