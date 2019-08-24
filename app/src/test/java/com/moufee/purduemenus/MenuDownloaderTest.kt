package com.moufee.purduemenus

import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.api.MenuDownloader
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.menus.DiningCourtMenu
import com.moufee.purduemenus.menus.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MenuDownloaderTest {

    @Mock
    private lateinit var webservice: Webservice

    private val diningCourtMenu: DiningCourtMenu = mock(DiningCourtMenu::class.java)
    private val dateString = "2019-05-29"
    private val testDate: DateTime = DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd"))

    @Before
    fun setup() = runBlockingTest {
        `when`(diningCourtMenu.location).thenReturn("Success")

        `when`(webservice.getMenu("Failure", dateString)).thenThrow(RuntimeException("Some Network Error Occurred"))
        `when`(webservice.getMenu("Success", dateString)).thenReturn(diningCourtMenu)
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
        assertThat(singleSuccess.getMenu("Success")).isNotNull()
        assertThat(singleSuccess.numMenus).isEqualTo(1)

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
        assertThat(singleSuccess.getMenu("Success")).isNotNull()
        assertThat(singleSuccess.getMenu("Failure")).isNull()
        assertThat(singleSuccess.numMenus).isEqualTo(1)
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
        assertThat(singleFailure.getMenu("Success")).isNull()
        assertThat(singleFailure.getMenu("Failure")).isNull()
        assertThat(singleFailure.numMenus).isEqualTo(0)
    }
}