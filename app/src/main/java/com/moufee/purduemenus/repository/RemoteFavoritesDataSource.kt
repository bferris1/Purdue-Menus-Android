package com.moufee.purduemenus.repository

import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.repository.data.menus.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class RemoteFavoritesDataSource @Inject constructor(private val webservice: Webservice) : FavoritesDataSource {
    override fun observeFavorites(): Flow<List<Favorite>> {
        return flow {
            emit(emptyList())
        }
    }

    override suspend fun getFavorites(ticket: String?): List<Favorite> {
        return try {
            webservice.getFavorites(ticket).Favorite.map { it.toFavorite() }
        } catch (e: Throwable) {
            Timber.e(e)
            emptyList()
        }
    }

    override suspend fun addFavorite(favorite: Favorite) {
        try {
            webservice.addFavorite(favorite.toApiFavorite())
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    override suspend fun removeFavorite(favorite: Favorite) {
        try {
            webservice.deleteFavorite(favorite.favoriteId)
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    override suspend fun addFavorites(favorites: List<Favorite>) {
        favorites.forEach {
            addFavorite(it)
        }
    }

    override suspend fun getFavoriteByItemId(favoriteId: String): Favorite? {
        return getFavorites().find { it.favoriteId == favoriteId }
    }

    override suspend fun removeAllFavorites() {
        TODO("Not Supported")
    }
}