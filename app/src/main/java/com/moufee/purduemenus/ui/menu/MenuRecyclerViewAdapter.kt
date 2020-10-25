package com.moufee.purduemenus.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moufee.purduemenus.databinding.FragmentMenuitemBinding
import com.moufee.purduemenus.databinding.StationHeaderBinding
import com.moufee.purduemenus.repository.data.menus.MenuItem
import com.moufee.purduemenus.repository.data.menus.Station
import java.util.*

/**
 * Created by Ben on 9/7/17.
 * A RecyclerViewAdapter for Menu Items
 */
class MenuRecyclerViewAdapter(private val mFavoriteListener: OnToggleFavoriteListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mListItems: MutableList<Any> = ArrayList()
    private var mFavoriteSet: Set<String> = HashSet()
    fun setFavoriteSet(favoriteSet: Set<String>) {
        mFavoriteSet = favoriteSet
        notifyDataSetChanged()
    }

    fun setStations(stations: List<Station>) {
        mListItems.clear()
        for ((name, items) in stations) {
            if (items.isNotEmpty()) {
                mListItems.add(name)
                mListItems.addAll(items)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_TYPE_HEADER) {
            val binding = StationHeaderBinding.inflate(layoutInflater, parent, false)
            return StationHeaderViewHolder(binding)
        }
        val itemBinding = FragmentMenuitemBinding.inflate(layoutInflater, parent, false)
        return MenuItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_HEADER && holder is StationHeaderViewHolder) {
            holder.bind(mListItems[position] as String)
        } else {
            val itemHolder = holder as MenuItemHolder
            val item = mListItems[position] as MenuItem
            itemHolder.bind(item, mFavoriteListener, mFavoriteSet.contains(item.id))
        }
    }

    override fun getItemCount(): Int {
        return mListItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mListItems[position] is String) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    fun isHeader(position: Int): Boolean {
        return getItemViewType(position) == VIEW_TYPE_HEADER
    }

    private inner class StationHeaderViewHolder(private val binding: StationHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stationName: String) {
            binding.stationName = stationName
            binding.executePendingBindings()
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}