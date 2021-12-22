package com.moufee.purduemenus.repository.data.menus

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Ben on 13/08/2017.
 * Represents one favorite, as returned by the dining API
 */
@Keep
@Entity
class Favorite(val itemName: String,
               val favoriteId: String,
               @field:PrimaryKey val itemId: String,
               val isVegetarian: Boolean) {
    override fun equals(other: Any?): Boolean {
        return other is Favorite && other.itemId == itemId
    }

    override fun hashCode(): Int {
        return itemId.hashCode()
    }

    override fun toString(): String {
        return itemName
    }
}