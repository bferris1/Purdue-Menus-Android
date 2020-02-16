package com.moufee.purduemenus.repository.data.menus;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Keep
public class OldMeal implements Serializable {

    private static final long serialVersionUID = 1L;
    private String Id;
    private String Name;
    private int Order;
    private String Status;
    private List<Station> Stations;

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public int getOrder() {
        return Order;
    }

    public String getStatus() {
        return Status;
    }


    public int getNumFavorites(@Nullable Set<String> favoriteIDs) {

        if (favoriteIDs == null)
            return 0;

        int count = 0;
        Set<String> countedFavorites = new HashSet<>();
        for (Station station :
                Stations) {
            for (MenuItem item :
                    station.getItems()) {
                if (favoriteIDs.contains(item.getId()) && !countedFavorites.contains(item.getId())) {
                    count++;
                    countedFavorites.add(item.getId());
                }
            }
        }
        return count;
    }

    public List<Station> getStations() {
        return Stations;
    }

    public boolean isOpen() {
        return Status != null && Status.equals("Open");
    }
}
