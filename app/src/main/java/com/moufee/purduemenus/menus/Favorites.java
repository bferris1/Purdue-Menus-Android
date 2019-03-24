package com.moufee.purduemenus.menus;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Keep;

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
