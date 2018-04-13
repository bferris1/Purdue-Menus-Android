package com.moufee.purduemenus.api;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.menus.Favorite;
import com.moufee.purduemenus.menus.Favorites;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFavoritesTask extends FavoriteTransactionTask<Favorites> {


    private static final String TAG = "DOWNLOAD_FAV_TASK";
    private Webservice mWebservice;
    private FavoriteDao mFavoriteDao;

    public UpdateFavoritesTask(OkHttpClient httpClient, SharedPreferences sharedPreferences, Webservice webservice, FavoriteDao favoriteDao) {
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
        uploadFavorites(response.body());
    }

    private void uploadFavorites(Favorites remoteFavorites) {
        if (remoteFavorites.getFavorites() == null) return;
        List<Favorite> localFavorites = mFavoriteDao.getAllFavorites();
        Set<Favorite> removeFavoriesSet = new HashSet<>(remoteFavorites.getFavorites());
        for (Favorite favorite :
                localFavorites) {
            if (!removeFavoriesSet.contains(favorite)) {
                Log.d(TAG, "uploadFavorites: " + favorite);
                mWebservice.addFavorite(favorite, null).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
            }
        }
    }


    private void saveFavorites(Favorites favorites) {
        if (favorites == null || favorites.getFavorites() == null)
            Log.d(TAG, "addFavorites: favorites were null!");
        else {
            mFavoriteDao.insertFavorites(favorites.getFavorites());
        }
    }

}
