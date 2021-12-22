package com.moufee.purduemenus.repository

import com.moufee.purduemenus.db.FavoriteDao
import com.moufee.purduemenus.repository.data.menus.Favorite
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalFavoritesDataSource @Inject constructor(private val favoriteDao: FavoriteDao) : FavoritesDataSource {
    override suspend fun getFavorites(ticket: String?): List<Favorite> {
        return favoriteDao.getAllFavorites()
    }

    override fun observeFavorites(): Flow<List<Favorite>> {
        return favoriteDao.getFavoritesFlow()
    }

    override suspend fun addFavorite(favorite: Favorite) {
        favoriteDao.insertFavorites(favorite)
    }

    override suspend fun removeFavorite(favorite: Favorite) {
        return favoriteDao.deleteByItemID(favorite.itemId)
    }

    override suspend fun addFavorites(favorites: List<Favorite>) {
        return favoriteDao.insertFavorites(favorites)
    }

    override suspend fun getFavoriteByItemId(favoriteId: String): Favorite? {
        return favoriteDao.getFavoriteByItemId(favoriteId)
    }

    override suspend fun removeAllFavorites() {
        favoriteDao.deleteAll()
    }
}