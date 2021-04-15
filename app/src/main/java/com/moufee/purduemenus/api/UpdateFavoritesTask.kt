package com.moufee.purduemenus.api

import android.content.SharedPreferences
import com.moufee.purduemenus.db.FavoriteDao
import com.moufee.purduemenus.repository.data.menus.Favorite
import com.moufee.purduemenus.repository.data.menus.Favorites
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class UpdateFavoritesTask(sharedPreferences: SharedPreferences,
                          private val mWebservice: Webservice,
                          private val mFavoriteDao: FavoriteDao) : AuthenticatedAPITask<Favorites>(sharedPreferences) {
    private var mTicket: String? = null
    fun setTicket(ticket: String?): UpdateFavoritesTask {
        mTicket = ticket
        return this
    }

    override val call: Call<Favorites>
        get() = mWebservice.getFavorites(mTicket)



    override fun onSuccess(response: Response<Favorites>) {
        val favorites = response.body()
        if (favorites != null) {
            saveFavorites(favorites)
            uploadFavorites(favorites)
        }
    }

    private fun uploadFavorites(remoteFavorites: Favorites?) {
        if (remoteFavorites?.favorites == null) return
        val localFavorites = mFavoriteDao.getAllFavorites()
        val remoteFavoritesSet: Set<Favorite> = HashSet(remoteFavorites.favorites)
        for (favorite in localFavorites) {
            if (!remoteFavoritesSet.contains(favorite)) {
                Timber.d("uploadFavorites: %s", favorite)
                mWebservice.addFavorite(favorite).enqueue(object : Callback<ResponseBody?> {
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

    private fun saveFavorites(favorites: Favorites?) {
        if (favorites?.favorites == null) Timber.d("favorites were null!") else {
            mFavoriteDao.insertFavorites(favorites.favorites)
        }
    }
}