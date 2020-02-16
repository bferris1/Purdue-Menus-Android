package com.moufee.purduemenus.repository.data.menus;

import androidx.annotation.Keep;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * A container for a list of favorites
 */
@Keep
public class Favorites {
    @Json(name = "Favorite")
    private List<Favorite> mFavorites;

    public List<Favorite> getFavorites() {
        return mFavorites;
    }
}
