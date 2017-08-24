package com.moufee.purduemenus.menus;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Ben on 13/08/2017.
 */
@Keep
public class Favorites {
    @SerializedName("Favorite")
    private ArrayList<Favorite> mFavorites;

    public ArrayList<Favorite> getFavorites() {
        return mFavorites;
    }
}
