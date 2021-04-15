package com.moufee.purduemenus.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.moufee.purduemenus.api.AuthenticatedAPITask
import com.moufee.purduemenus.api.UpdateFavoritesTask
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.db.FavoriteDao
import com.moufee.purduemenus.repository.data.menus.Favorite
import com.moufee.purduemenus.repository.data.menus.MenuItem
import com.moufee.purduemenus.util.AppExecutors
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by Ben on 13/08/2017.
 * Repository for Favorites
 */
class FavoritesRepository @Inject constructor(private val mFavoriteDao: FavoriteDao,
                                              private val mAppExecutors: AppExecutors,
                                              private val mWebservice: Webservice,
                                              private val authDataSource: AuthDataSource,
                                              private val mSharedPreferences: SharedPreferences) {
    val favorites: LiveData<List<Favorite>>
        get() = mFavoriteDao.loadAllFavorites()
    val favoriteIDSet: LiveData<Set<String>>
        get() = Transformations.map(mFavoriteDao.getFavoriteIDs()) { favoriteIDs: List<String>? ->
            if (favoriteIDs != null) return@map HashSet(favoriteIDs)
            HashSet()
        }

    fun updateFavoritesFromWeb(ticket: String?) {
        mAppExecutors.networkIO().execute(UpdateFavoritesTask(mSharedPreferences, mWebservice, mFavoriteDao).setTicket(ticket))
    }

    fun addFavorite(item: MenuItem) {
        val favorite = Favorite(item.name, UUID.randomUUID().toString(), item.id, item.isVegetarian)
        mAppExecutors.diskIO().execute { mFavoriteDao.insertFavorites(favorite) }
        if (authDataSource.isLoggedIn()) mAppExecutors.networkIO()
                .execute(object : AuthenticatedAPITask<ResponseBody>(mSharedPreferences) {
                    override val call: Call<ResponseBody>
                        get() = mWebservice.addFavorite(favorite)

                    override fun onSuccess(response: Response<ResponseBody>) {
                        Timber.d("onSuccess: %s", response)
                        updateFavoritesFromWeb(null)
                    }
                })
    }

    fun removeFavorite(item: MenuItem) {
        if (authDataSource.isLoggedIn()) mAppExecutors.diskIO().execute {
            val favorite = mFavoriteDao.getFavoriteByItemId(item.id)
            if (favorite != null) {
                mAppExecutors.networkIO().execute(object : AuthenticatedAPITask<ResponseBody>(mSharedPreferences) {
                    override val call: Call<ResponseBody>
                        get() = mWebservice.deleteFavorite(favorite.favoriteId)

                    override fun onSuccess(response: Response<ResponseBody>) {
                        Timber.d("onSuccess: %s", response)
                    }
                })
            }
            mFavoriteDao.deleteByItemID(item.id)
        } else {
            mAppExecutors.diskIO().execute { mFavoriteDao.deleteByItemID(item.id) }
        }
    }

    fun clearLocalFavorites() {
        mAppExecutors.diskIO().execute { mFavoriteDao.deleteAll() }
    }
}