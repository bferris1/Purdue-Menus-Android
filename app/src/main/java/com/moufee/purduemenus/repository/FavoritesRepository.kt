package com.moufee.purduemenus.repository

import com.moufee.purduemenus.api.models.RemoteFavorite
import com.moufee.purduemenus.repository.data.menus.Favorite
import com.moufee.purduemenus.repository.data.menus.MenuItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ben on 13/08/2017.
 * Repository for Favorites
 */
class FavoritesRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    @Named(FavoritesDataSource.LOCAL) private val localDataSource: FavoritesDataSource,
    @Named(FavoritesDataSource.REMOTE) private val remoteDataSource: FavoritesDataSource,
) {
    val favorites: Flow<List<Favorite>>
        get() = localDataSource.observeFavorites()
    val favoriteIDSet: Flow<Set<String>>
        get() = favorites.map { favoriteList -> favoriteList.map { it.itemId }.toSet() }

    suspend fun updateFavoritesFromWeb(ticket: String?) {
        val remoteFavorites = remoteDataSource.getFavorites(ticket)
        localDataSource.addFavorites(remoteFavorites)
        val localFavorites = localDataSource.getFavorites()
        val localOnlyFavorites = localFavorites.subtract(remoteFavorites)
        for (favorite in localOnlyFavorites) {
            remoteDataSource.addFavorite(favorite)
        }
    }

    suspend fun addFavorite(item: MenuItem) {
        val favorite = Favorite(
            itemName = item.name,
            favoriteId = UUID.randomUUID().toString(),
            itemId = item.id,
            isVegetarian = item.isVegetarian
        )
        localDataSource.addFavorite(favorite)
        if (authDataSource.isLoggedIn()) {
            try {
                remoteDataSource.addFavorite(favorite)
                updateFavoritesFromWeb(null)

            } catch (t: Throwable) {
                Timber.e(t)
            }
        }
    }

    suspend fun removeFavorite(item: MenuItem) {
        localDataSource.getFavoriteByItemId(item.id)?.let { favorite ->
            if (authDataSource.isLoggedIn()) {
                remoteDataSource.removeFavorite(favorite)
            }
            localDataSource.removeFavorite(favorite)
        }
    }

    suspend fun clearLocalFavorites() {
        localDataSource.removeAllFavorites()
    }
}


fun Favorite.toApiFavorite() = RemoteFavorite(itemName, favoriteId, itemId, isVegetarian)

fun RemoteFavorite.toFavorite() = Favorite(ItemName, FavoriteId, ItemId, IsVegetarian)