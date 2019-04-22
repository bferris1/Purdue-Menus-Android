package com.moufee.purduemenus.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moufee.purduemenus.menus.Location

@Dao
interface LocationDao {
    @Query("SELECT * from location ORDER BY displayOrder")
    fun getAll(): LiveData<List<Location>>

    @Query("SELECT * from location ORDER BY displayOrder")
    fun getAllList(): List<Location>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(locations: List<Location>)

    @Update
    fun updateLocations(locations: List<Location>)

    @Delete
    fun delete(location: Location)

}