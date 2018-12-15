package com.moufee.purduemenus.menus;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ben on 9/5/17.
 * Represent one item on the menu
 */
@Keep
public class MenuItem implements Serializable {
    @SerializedName("ID")
    private String id;
    private String name;
    private boolean isVegetarian;
    private boolean isFavorite = false;

    public MenuItem(String name) {
        this.name = name;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + " :" + name;
    }
}
