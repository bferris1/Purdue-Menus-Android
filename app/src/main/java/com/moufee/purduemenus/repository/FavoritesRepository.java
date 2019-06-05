package com.moufee.purduemenus.repository;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.moufee.purduemenus.api.AuthenticatedAPITask;
import com.moufee.purduemenus.api.UpdateFavoritesTask;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.menus.Favorite;
import com.moufee.purduemenus.menus.MenuItem;
import com.moufee.purduemenus.util.AppExecutors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ben on 13/08/2017.
 * Repository for Favorites
 */

public class FavoritesRepository {

    private FavoriteDao mFavoriteDao;
    private AppExecutors mAppExecutors;
    private Webservice mWebservice;
    private SharedPreferences mSharedPreferences;
    private static final String TAG = "FAVORITES_REPOSITORY";

    @Inject
    public FavoritesRepository(FavoriteDao favoriteDao, AppExecutors appExecutors, Webservice webservice, SharedPreferences sharedPreferences) {
        mFavoriteDao = favoriteDao;
        mAppExecutors = appExecutors;
        mWebservice = webservice;
        mSharedPreferences = sharedPreferences;
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

    public void updateFavoritesFromWeb(@Nullable String ticket) {
        mAppExecutors.networkIO().execute(new UpdateFavoritesTask(mSharedPreferences, mWebservice, mFavoriteDao).setTicket(ticket));
    }

    public void addFavorite(MenuItem item) {
        Favorite favorite = new Favorite(item.getName(), UUID.randomUUID().toString(), item.getId(), item.isVegetarian());
        mAppExecutors.diskIO().execute(() -> mFavoriteDao.insertFavorites(favorite));
        if (mSharedPreferences.getBoolean("logged_in", false))
            mAppExecutors.networkIO().execute(new AuthenticatedAPITask<ResponseBody>(mSharedPreferences) {
                @Override
                public Call<ResponseBody> getCall() {
                    return mWebservice.addFavorite(favorite);
                }

                @Override
                public void onSuccess(Response<ResponseBody> response) {
                    Log.d(TAG, "onSuccess: " + response);
                    updateFavoritesFromWeb(null);
                }
            });
    }

    public void removeFavorite(MenuItem item) {
        if (mSharedPreferences.getBoolean("logged_in", false))
            mAppExecutors.diskIO().execute(() -> {
                Favorite favorite = mFavoriteDao.getFavoriteByItemId(item.getId());
                Log.d(TAG, "removeFavorite: deleting favorite" + favorite + " " + favorite.favoriteId);
                mAppExecutors.networkIO().execute(new AuthenticatedAPITask<ResponseBody>(mSharedPreferences) {
                    @Override
                    public Call<ResponseBody> getCall() {
                        return mWebservice.deleteFavorite(favorite.favoriteId);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> response) {
                        Log.d(TAG, "onSuccess: " + response);
                    }
                });
                mFavoriteDao.deleteByItemID(item.getId());
            });
        else
            mAppExecutors.diskIO().execute(() -> mFavoriteDao.deleteByItemID(item.getId()));

    }

    public void clearLocalFavorites() {
        mAppExecutors.diskIO().execute(() -> mFavoriteDao.deleteAll());
    }
}
