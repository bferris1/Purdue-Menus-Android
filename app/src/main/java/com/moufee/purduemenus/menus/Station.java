package com.moufee.purduemenus.menus;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Keep;

@Keep
public class Station implements Serializable {

    private static final long serialVersionUID = 1L;
    private String Name;
    private List<MenuItem> Items;

    public String getName() {
        return Name;
    }

    public List<MenuItem> getItems() {
        return Items;
    }

    public int getNumItems() {
        if (Items == null)
            return 0;
        return Items.size();
    }

    public MenuItem getItem(int index) {
        return Items.get(index);
    }

}
