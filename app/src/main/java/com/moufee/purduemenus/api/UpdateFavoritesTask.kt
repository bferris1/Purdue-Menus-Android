package com.moufee.purduemenus.api

import android.content.SharedPreferences
import com.moufee.purduemenus.api.models.ApiFavoritesResponse
import com.moufee.purduemenus.db.FavoriteDao
import com.moufee.purduemenus.repository.data.menus.Favorite
import com.moufee.purduemenus.repository.toApiFavorite
import com.moufee.purduemenus.repository.toFavorite
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class UpdateFavoritesTask(sharedPreferences: SharedPreferences,
                          private val mWebservice: Webservice,
                          private val mFavoriteDao: FavoriteDao) : AuthenticatedAPITask<ApiFavoritesResponse>(sharedPreferences) {
    private var mTicket: String? = null
    fun setTicket(ticket: String?): UpdateFavoritesTask {
        mTicket = ticket
        return this
    }

    override val call: Call<ApiFavoritesResponse>
        get() = mWebservice.getFavorites(mTicket)



    override fun onSuccess(response: Response<ApiFavoritesResponse>) {
        val favorites = response.body()?.Favorite?.map { it.toFavorite() }
        if (favorites != null) {
            saveFavorites(favorites)
            uploadFavorites(favorites)
        }
    }

    private fun uploadFavorites(remoteFavorites: List<Favorite>) {
        val localFavorites = mFavoriteDao.getAllFavorites()
        val remoteFavoritesSet = remoteFavorites.toSet()
        for (favorite in localFavorites) {
            if (!remoteFavoritesSet.contains(favorite)) {
                Timber.d("uploadFavorites: %s", favorite)
                mWebservice.addFavorite(favorite.toApiFavorite()).enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        Timber.d("onResponse: %s", response)
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Timber.e(t)
                    }
                })
            }
        }
    }

    private fun saveFavorites(favorites: List<Favorite>) {
        mFavoriteDao.insertFavorites(favorites)
    }
}