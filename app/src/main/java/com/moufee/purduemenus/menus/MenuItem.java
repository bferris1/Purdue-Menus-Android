package com.moufee.purduemenus.menus;

import androidx.annotation.Keep;

import java.io.Serializable;

/**
 * Created by Ben on 9/5/17.
 * Represent one item on the menu
 */
@Keep
public class MenuItem implements Serializable {
    private String ID;
    private String Name;
    private boolean IsVegetarian;

    public MenuItem(String name) {
        this.Name = name;
    }

    public String getName() {
        return Name;
    }

    public boolean isVegetarian() {
        return IsVegetarian;
    }

    public String getId() {
        return ID;
    }

    @Override
    public String toString() {
        return ID + " :" + Name;
    }
}
