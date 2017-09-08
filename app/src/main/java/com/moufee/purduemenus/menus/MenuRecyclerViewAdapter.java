package com.moufee.purduemenus.menus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.databinding.FragmentMenuitemBinding;
import com.moufee.purduemenus.ui.menu.MenuItemHolder;

import java.util.List;

/**
 * Created by Ben on 9/7/17.
 * A RecyclerViewAdapter for Menu Items
 */

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiningCourtMenu.Station> stations;
    private int totalItems;

    public final static int VIEW_TYPE_HEADER = 0;
    public final static int VIEW_TYPE_ITEM = 1;

    public void setStations(List<DiningCourtMenu.Station> stations){
        this.stations = stations;
        int total = 0;
        for (DiningCourtMenu.Station station:
                stations) {
            total += station.getNumItems();
        }
        total += stations.size();
        totalItems = total;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_header, parent, false);
            return new StationHeaderViewHolder(view);
        }
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        FragmentMenuitemBinding itemBinding =
                FragmentMenuitemBinding.inflate(layoutInflater, parent, false);
        return new MenuItemHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER && holder instanceof StationHeaderViewHolder){
            ((StationHeaderViewHolder) holder).stationName
                    .setText(stations.get(getSectionIndex(position)).getName());
        }else {
            MenuItemHolder itemHolder = (MenuItemHolder) holder;
            itemHolder.bind(getMenuItemForPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return totalItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (getPositionInSection(position) == 0)
            return VIEW_TYPE_HEADER;
        return VIEW_TYPE_ITEM;
    }

    private MenuItem getMenuItemForPosition(int position){
        return stations.get(getSectionIndex(position)).getItem(getPositionInSection(position) - 1);
    }

    private int getPositionInSection(int position){
        int currentStationStartIndex = 0;
        for (DiningCourtMenu.Station station : stations){
            if (position >= currentStationStartIndex && position <= currentStationStartIndex + station.getNumItems()){
                return position - currentStationStartIndex;
            }
            currentStationStartIndex += station.getNumItems() + 1;
        }
        throw new IndexOutOfBoundsException("Position is outside the allowed range.");
    }
    private int getSectionIndex(int position){
        int currentStationStartIndex = 0;
        for (int i = 0; i < stations.size(); i++) {
            DiningCourtMenu.Station station = stations.get(i);
            if (position >= currentStationStartIndex && position <= currentStationStartIndex + station.getNumItems()){
                return i;
            }
            currentStationStartIndex += station.getNumItems() + 1;
        }
        throw new IndexOutOfBoundsException("Position is outside the allowed range.");
    }


    private class StationHeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView stationName;

        StationHeaderViewHolder(View view) {
            super(view);
            stationName = (TextView) view.findViewById(R.id.station_name_text_view);
        }
    }
}
