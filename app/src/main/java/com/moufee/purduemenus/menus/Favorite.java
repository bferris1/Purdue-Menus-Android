package com.moufee.purduemenus.menus;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

/**
 * Created by Ben on 13/08/2017.
 * Represents one favorite, as returned by the dining API
 */
@Keep
@Entity
public class Favorite {
    @NonNull
    public final String itemName;

    @NonNull
    public final String favoriteId;
    @PrimaryKey
    @NonNull
    public final String itemId;
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
        return obj != null && obj instanceof Favorite && ((Favorite) obj).itemId.equals(itemId);
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
