package com.moufee.purduemenus.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moufee.purduemenus.repository.data.menus.Favorite
import kotlinx.coroutines.flow.Flow

@Dao interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertFavorites(favorites: List<Favorite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertFavorites(vararg favorites: Favorite)

    @Query("SELECT * FROM favorite") fun getFavoritesFlow(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorite") suspend fun getAllFavorites(): List<Favorite>

    @Query("SELECT itemId FROM favorite") fun getFavoriteIDs(): Flow<List<String>>

    @Query("SELECT * FROM favorite WHERE itemId = :itemID LIMIT 1") suspend fun getFavoriteByItemId(itemID: String): Favorite?

    @Query("DELETE FROM favorite WHERE itemId = :itemID") suspend fun deleteByItemID(itemID: String)

    @Query("DELETE FROM favorite") suspend fun deleteAll()
}