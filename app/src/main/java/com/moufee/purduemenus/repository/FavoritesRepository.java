package com.moufee.purduemenus.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.menus.Favorite;
import com.moufee.purduemenus.menus.MenuItem;
import com.moufee.purduemenus.util.AppExecutors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Ben on 13/08/2017.
 * Repository for Favorites
 */

public class FavoritesRepository {

    private FavoriteDao mFavoriteDao;
    private AppExecutors mAppExecutors;

    @Inject
    public FavoritesRepository(FavoriteDao favoriteDao, AppExecutors appExecutors) {
        mFavoriteDao = favoriteDao;
        mAppExecutors = appExecutors;
    }


    public LiveData<List<Favorite>> getFavorites() {
        return mFavoriteDao.loadAllFavorites();
    }

    public LiveData<Set<String>> getFavoriteIDSet() {
        return Transformations.map(mFavoriteDao.getFavoriteIDs(), favoriteIDs -> {
            if (favoriteIDs != null)
                return new HashSet<>(favoriteIDs);
            return new HashSet<>();

        });
    }

    public void addFavorite(MenuItem item) {
        Favorite favorite = new Favorite(item.getName(), UUID.randomUUID().toString(), item.getId(), item.isVegetarian());
        mAppExecutors.diskIO().execute(() -> mFavoriteDao.insertFavorites(favorite));
    }

    public void removeFavorite(MenuItem item) {
        mAppExecutors.diskIO().execute(() -> mFavoriteDao.deleteByItemID(item.getId()));
    }

    public void clearLocalFavorites() {
        mAppExecutors.diskIO().execute(() -> mFavoriteDao.deleteAll());
    }
}
