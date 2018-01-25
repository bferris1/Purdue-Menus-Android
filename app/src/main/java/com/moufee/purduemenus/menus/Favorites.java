package com.moufee.purduemenus.menus;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * A container for a list of favorites
 */
@Keep
public class Favorites {
    @SerializedName("Favorite")
    private ArrayList<Favorite> mFavorites;

    public ArrayList<Favorite> getFavorites() {
        return mFavorites;
    }
}
