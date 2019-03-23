package com.moufee.purduemenus.menus;

import com.squareup.moshi.Json;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Ben on 13/08/2017.
 * Represents one favorite, as returned by the dining API
 */
@Keep
@Entity
public class Favorite {
    @NonNull
    @Json(name = "ItemName")
    public final String itemName;

    @NonNull
    @Json(name = "FavoriteId")
    public final String favoriteId;

    @PrimaryKey
    @NonNull
    @Json(name = "ItemId")
    public final String itemId;

    @Json(name = "IsVegetarian")
    @NonNull
    public final Boolean isVegetarian;

    public Favorite(@NonNull String itemName, @NonNull String favoriteId, @NonNull String itemId, @NonNull Boolean isVegetarian) {
        this.itemName = itemName;
        this.favoriteId = favoriteId;
        this.itemId = itemId;
        this.isVegetarian = isVegetarian;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Favorite && ((Favorite) obj).itemId.equals(itemId);
    }

    @Override
    public int hashCode() {
        return itemId.hashCode();
    }

    @Override
    public String toString() {
        return itemName;
    }
}
