package com.moufee.purduemenus.api.models

data class RemoteFavorite(val ItemName: String, val FavoriteId: String, val ItemId: String, val IsVegetarian: Boolean)

data class ApiFavoritesResponse(val Favorite: List<RemoteFavorite>)