package com.moufee.purduemenus.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.menus.DiningCourtMenu;


import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by Ben on 14/07/2017.
 * A SectionedRecyclerView Section for a station in a dining court
 */

public class StationSection extends StatelessSection {
    private DiningCourtMenu.Station mStation;
    private boolean mShowVegetarianIcon;

    public StationSection(DiningCourtMenu.Station station) {
        super(new SectionParameters.Builder(R.layout.fragment_menuitem)
                .headerResourceId(R.layout.station_header)
                .build());
        mStation = station;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        StationHeaderViewHolder headerHolder = (StationHeaderViewHolder) holder;
        headerHolder.stationName.setText(mStation.getName());
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new StationHeaderViewHolder(view);
    }

    @Override
    public int getContentItemsTotal() {
        return mStation.getItems().size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MenuItemHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MenuItemHolder itemHolder = (MenuItemHolder) holder;
        itemHolder.mItem = mStation.getItems().get(position);
        if (itemHolder.mItem.isVegetarian())
            itemHolder.mVegetarianIcon.setVisibility(View.VISIBLE);
        else
            itemHolder.mVegetarianIcon.setVisibility(View.GONE);

        itemHolder.mItemNameTextView.setText(mStation.getItems().get(position).getName());

    }


    private class StationHeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView stationName;

        StationHeaderViewHolder(View view) {
            super(view);

            stationName = (TextView) view.findViewById(R.id.station_name_text_view);
        }
    }
}
