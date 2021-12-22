package com.moufee.purduemenus.api.models

data class ApiFavorite(val ItemName: String, val FavoriteId: String, val ItemId: String, val IsVegetarian: Boolean)

data class ApiFavoritesResponse(val Favorite: List<ApiFavorite>)