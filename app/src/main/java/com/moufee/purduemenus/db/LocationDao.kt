package com.moufee.purduemenus.db

import androidx.room.*
import com.moufee.purduemenus.menus.Location

@Dao
interface LocationDao {
    @Query("SELECT * from location ORDER BY displayOrder")
    fun getall(): List<Location>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg location: Location)

    @Delete
    fun delete(location: Location)

}