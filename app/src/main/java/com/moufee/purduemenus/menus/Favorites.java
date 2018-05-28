package com.moufee.purduemenus.menus;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for a list of favorites
 */
@Keep
public class Favorites {
    @SerializedName("Favorite")
    private ArrayList<Favorite> mFavorites;

    public List<Favorite> getFavorites() {
        return mFavorites;
    }
}
