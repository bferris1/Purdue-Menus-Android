package com.moufee.purduemenus.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.moufee.purduemenus.menus.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertFavorites(List<Favorite> favorites);

    @Query("SELECT * FROM favorite")
    public LiveData<List<Favorite>> loadAllFavorites();

    @Query("SELECT * FROM favorite WHERE itemId = :itemID LIMIT 1")
    public Favorite getFavoriteByItemId(String itemID);

    @Delete
    public void deleteFavorites(Favorite... favorites);

    @Query("DELETE FROM favorite")
    public void deleteAll();
}
