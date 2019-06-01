package com.moufee.purduemenus

import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.api.MenuDownloader
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.menus.DiningCourtMenu
import com.moufee.purduemenus.menus.Location
import io.reactivex.Single
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class MenuDownloaderTest {

    @Mock
    private lateinit var webservice: Webservice

    private val diningCourtMenu: DiningCourtMenu = mock(DiningCourtMenu::class.java)
    private val dateString = "2019-05-29"
    private val testDate: DateTime = DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd"))

    @Before
    fun setup() {
        `when`(diningCourtMenu.location).thenReturn("Success")

        `when`(webservice.getMenu("Failure", dateString)).thenReturn(Single.error(IOException("Some Problem Occurred")))
        `when`(webservice.getMenu("Success", dateString)).thenReturn(Single.just(diningCourtMenu))
    }

    @Test
    fun testFailure() {

        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Success", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        val singleSuccess = downloader.getMenus(testDate, locations)
        val fullDayMenu = singleSuccess.blockingGet()
        verify(webservice).getMenu("Failure", dateString)
        verify(webservice).getMenu("Success", dateString)
        assertThat(fullDayMenu.getMenu("Success")).isNotNull()
        assertThat(fullDayMenu.getMenu("Failure")).isNull()
        assertThat(fullDayMenu.numMenus).isEqualTo(1)
    }

    @Test
    fun testAllFailure() {

        val downloader = MenuDownloader(webservice)
        val locations: MutableList<Location> = ArrayList()
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        locations.add(Location("Failure", "TEST", "Test Dining Court", 0))
        val singleFailure = downloader.getMenus(testDate, locations)
        val fullDayMenu = singleFailure.blockingGet()
        verify(webservice, times(3)).getMenu("Failure", dateString)
        assertThat(fullDayMenu.getMenu("Success")).isNull()
        assertThat(fullDayMenu.getMenu("Failure")).isNull()
        assertThat(fullDayMenu.numMenus).isEqualTo(0)
    }
}