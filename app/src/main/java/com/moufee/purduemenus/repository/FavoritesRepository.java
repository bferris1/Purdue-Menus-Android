package com.moufee.purduemenus.repository;

import android.arch.lifecycle.LiveData;

import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.menus.Favorite;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Ben on 13/08/2017.
 */

public class FavoritesRepository {

    private FavoriteDao mFavoriteDao;

    @Inject
    public FavoritesRepository(FavoriteDao favoriteDao) {
        mFavoriteDao = favoriteDao;
    }

    public LiveData<List<Favorite>> getFavorites() {
        return mFavoriteDao.loadAllFavorites();
    }
}
