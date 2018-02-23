package com.moufee.purduemenus.menus;

import android.support.annotation.Keep;

/**
 * Created by Ben on 13/08/2017.
 * Represents one favorite, as returned by the dining API
 */
@Keep
public class Favorite {

    private String itemName;
    private String favoriteId;
    private String itemId;
    private Boolean isVegetarian;

    public String getItemName() {
        return itemName;
    }

    public String getFavoriteId() {
        return favoriteId;
    }

    public String getItemId() {
        return itemId;
    }

    public Boolean isVegetarian() {
        return isVegetarian;
    }

    @Override
    public String toString() {
        return itemName;
    }
}
