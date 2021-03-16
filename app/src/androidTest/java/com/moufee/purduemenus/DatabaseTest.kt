package com.moufee.purduemenus

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moufee.purduemenus.db.AppDatabase
import com.moufee.purduemenus.db.FavoriteDao
import com.moufee.purduemenus.db.LocationDao
import com.moufee.purduemenus.repository.data.menus.Favorite
import com.moufee.purduemenus.repository.data.menus.Location
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var locationDao: LocationDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
        favoriteDao = db.favoriteDao()
        locationDao = db.locationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeFavoriteAndQuery() {
        val favorite = Favorite("Salad", "12345", "54321", true)
        favoriteDao.insertFavorites(favorite)
        val byId = favoriteDao.getFavoriteByItemId("54321")!!
        assertThat(byId.itemName, equalTo("Salad"))
        assertThat(byId.favoriteId, equalTo("12345"))
        assertThat(byId.itemName, equalTo("Salad"))
        assertThat(byId.isVegetarian, equalTo(true))
    }

    @Test
    fun testLocation() {
        val location = Location("Windsor", "WIND", "Windsor Dining Court", 0)
        locationDao.insertAll(listOf(location))
        val allLocations: List<Location> = locationDao.getAllList()
        assertThat(allLocations.size, equalTo(1))
        assertThat(allLocations[0].FormalName, equalTo("Windsor Dining Court"))
        assertThat(allLocations[0].LocationId, equalTo("WIND"))
        assertThat(allLocations[0].Name, equalTo("Windsor"))
        assertThat(allLocations[0].displayOrder, equalTo(0))
    }
}