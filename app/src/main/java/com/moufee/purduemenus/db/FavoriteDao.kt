package com.moufee.purduemenus.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moufee.purduemenus.repository.data.menus.Favorite

@Dao interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertFavorites(favorites: List<Favorite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertFavorites(vararg favorites: Favorite)

    @Query("SELECT * FROM favorite") fun loadAllFavorites(): LiveData<List<Favorite>>

    @Query("SELECT * FROM favorite") fun getAllFavorites(): List<Favorite>

    @Query("SELECT itemId FROM favorite") fun getFavoriteIDs(): LiveData<List<String>>

    @Query("SELECT * FROM favorite WHERE itemId = :itemID LIMIT 1") fun getFavoriteByItemId(itemID: String): Favorite?

    @Query("DELETE FROM favorite WHERE itemId = :itemID") fun deleteByItemID(itemID: String)

    @Delete fun deleteFavorites(vararg favorites: Favorite)

    @Query("DELETE FROM favorite") fun deleteAll()
}