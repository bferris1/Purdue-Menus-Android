package com.moufee.purduemenus.menus;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import androidx.annotation.Keep;

/**
 * Created by Ben on 9/5/17.
 * Represent one item on the menu
 */
@Keep
public class MenuItem implements Serializable {
    @SerializedName("ID")
    private String Id;
    private String Name;
    private boolean isVegetarian;

    public MenuItem(String name) {
        this.Name = name;
    }

    public String getName() {
        return Name;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public String getId() {
        return Id;
    }

    @Override
    public String toString() {
        return Id + " :" + Name;
    }
}
