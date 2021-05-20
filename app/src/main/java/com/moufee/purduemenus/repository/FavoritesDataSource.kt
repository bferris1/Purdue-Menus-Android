package com.moufee.purduemenus.repository

import com.moufee.purduemenus.repository.data.menus.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoritesDataSource {

    fun observeFavorites(): Flow<List<Favorite>>
    suspend fun getFavorites(ticket: String? = null): List<Favorite>
    suspend fun addFavorite(favorite: Favorite)
    suspend fun addFavorites(favorites: List<Favorite>)
    suspend fun removeFavorite(favorite: Favorite)
    suspend fun getFavoriteByItemId(favoriteId: String): Favorite?
    suspend fun removeAllFavorites()

    companion object {
        const val REMOTE = "FAVORITES_REMOTE_SOURCE"
        const val LOCAL = "FAVORITES_LOCAL_SOURCE"
    }
}