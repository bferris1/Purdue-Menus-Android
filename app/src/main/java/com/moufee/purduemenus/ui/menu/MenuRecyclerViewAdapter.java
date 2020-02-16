package com.moufee.purduemenus.ui.menu;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moufee.purduemenus.databinding.FragmentMenuitemBinding;
import com.moufee.purduemenus.databinding.StationHeaderBinding;
import com.moufee.purduemenus.repository.data.menus.MenuItem;
import com.moufee.purduemenus.repository.data.menus.Station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ben on 9/7/17.
 * A RecyclerViewAdapter for Menu Items
 */

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mListItems = new ArrayList<>();
    private Set<String> mFavoriteSet = new HashSet<>();
    private OnToggleFavoriteListener mFavoriteListener;

    private final static int VIEW_TYPE_HEADER = 0;
    private final static int VIEW_TYPE_ITEM = 1;

    public MenuRecyclerViewAdapter(OnToggleFavoriteListener favoriteListener) {
        mFavoriteListener = favoriteListener;
    }

    public void setFavoriteSet(Set<String> favoriteSet) {
        mFavoriteSet = favoriteSet;
        notifyDataSetChanged();
    }

    public void setStations(List<Station> stations) {
        mListItems.clear();
        for (Station station : stations) {
            if (station.getItems().size() > 0) {
                mListItems.add(station.getName());
                mListItems.addAll(station.getItems());
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            StationHeaderBinding binding = StationHeaderBinding.inflate(layoutInflater, parent, false);
            return new StationHeaderViewHolder(binding);
        }
        FragmentMenuitemBinding itemBinding =
                FragmentMenuitemBinding.inflate(layoutInflater, parent, false);
        return new MenuItemHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER && holder instanceof StationHeaderViewHolder) {
            StationHeaderViewHolder stationHolder = (StationHeaderViewHolder) holder;
            stationHolder.bind((String) mListItems.get(position));
        } else {
            MenuItemHolder itemHolder = (MenuItemHolder) holder;
            MenuItem item = (MenuItem) mListItems.get(position);
            itemHolder.bind(item, mFavoriteListener, mFavoriteSet.contains(item.getId()));
        }
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mListItems.get(position) instanceof String)
            return VIEW_TYPE_HEADER;
        return VIEW_TYPE_ITEM;
    }

    public boolean isHeader(int position) {
        return getItemViewType(position) == VIEW_TYPE_HEADER;
    }

    private class StationHeaderViewHolder extends RecyclerView.ViewHolder {

        private StationHeaderBinding binding;

        StationHeaderViewHolder(StationHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(String stationName) {
            binding.setStationName(stationName);
            binding.executePendingBindings();
        }
    }
}
