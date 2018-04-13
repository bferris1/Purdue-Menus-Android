package com.moufee.purduemenus.api;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.menus.Favorites;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadFavoritesTask extends FavoriteTransactionTask<Favorites> {


    private static final String TAG = "DOWNLOAD_FAV_TASK";
    private Webservice mWebservice;
    private FavoriteDao mFavoriteDao;

    public DownloadFavoritesTask(OkHttpClient httpClient, SharedPreferences sharedPreferences, Webservice webservice, FavoriteDao favoriteDao) {
        super(httpClient, sharedPreferences);
        mWebservice = webservice;
        mFavoriteDao = favoriteDao;
    }

    @Override
    public Call<Favorites> getCall(@Nullable String ticket) {
        return mWebservice.getFavorites(ticket);
    }

    @Override
    public void onSuccess(Response<Favorites> response) {
        saveFavorites(response.body());
    }


    private void saveFavorites(Favorites favorites) {
        if (favorites == null)
            Log.d(TAG, "addFavorites: favorites were null!");
        else {
            mFavoriteDao.insertFavorites(favorites.getFavorites());
        }
    }

}
