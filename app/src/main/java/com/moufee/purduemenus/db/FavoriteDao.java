package com.moufee.purduemenus.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moufee.purduemenus.repository.data.menus.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertFavorites(List<Favorite> favorites);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertFavorites(Favorite... favorites);

    @Query("SELECT * FROM favorite")
    public LiveData<List<Favorite>> loadAllFavorites();

    @Query("SELECT * FROM favorite")
    public List<Favorite> getAllFavorites();

    @Query("SELECT itemId FROM favorite")
    public LiveData<List<String>> getFavoriteIDs();

    @Query("SELECT * FROM favorite WHERE itemId = :itemID LIMIT 1")
    public Favorite getFavoriteByItemId(String itemID);

    @Query("DELETE FROM favorite WHERE itemId = :itemID")
    public void deleteByItemID(String itemID);

    @Delete
    public void deleteFavorites(Favorite... favorites);

    @Query("DELETE FROM favorite")
    public void deleteAll();
}
