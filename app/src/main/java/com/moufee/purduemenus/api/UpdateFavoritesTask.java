package com.moufee.purduemenus.api;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.repository.data.menus.Favorite;
import com.moufee.purduemenus.repository.data.menus.Favorites;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFavoritesTask extends AuthenticatedAPITask<Favorites> {


    private static final String TAG = "DOWNLOAD_FAV_TASK";
    private Webservice mWebservice;
    private FavoriteDao mFavoriteDao;
    private String mTicket = null;

    public UpdateFavoritesTask(SharedPreferences sharedPreferences, Webservice webservice, FavoriteDao favoriteDao) {
        super(sharedPreferences);
        mWebservice = webservice;
        mFavoriteDao = favoriteDao;
    }

    public UpdateFavoritesTask setTicket(String ticket) {
        this.mTicket = ticket;
        return this;
    }

    @Override
    public Call<Favorites> getCall() {
        return mWebservice.getFavorites(mTicket);
    }

    @Override
    public void onSuccess(Response<Favorites> response) {
        Favorites favorites = response.body();
        if (favorites != null) {
            saveFavorites(favorites);
            uploadFavorites(favorites);
        }
    }

    private void uploadFavorites(Favorites remoteFavorites) {
        if (remoteFavorites == null || remoteFavorites.getFavorites() == null) return;
        List<Favorite> localFavorites = mFavoriteDao.getAllFavorites();
        Set<Favorite> remoteFavoritesSet = new HashSet<>(remoteFavorites.getFavorites());
        for (Favorite favorite :
                localFavorites) {
            if (!remoteFavoritesSet.contains(favorite)) {
                Log.d(TAG, "uploadFavorites: " + favorite);
                mWebservice.addFavorite(favorite).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
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
