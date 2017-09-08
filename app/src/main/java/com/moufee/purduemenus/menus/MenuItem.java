package com.moufee.purduemenus.menus;

import android.support.annotation.Keep;

/**
 * Created by Ben on 9/5/17.
 * Represent one item on the menu
 */
@Keep
public class MenuItem {
    private String id;
    private String name;
    private Boolean isVegetarian;

    public MenuItem(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public Boolean isVegetarian() {
        return isVegetarian;
    }

    public String getId() {
        return id;
    }

}
