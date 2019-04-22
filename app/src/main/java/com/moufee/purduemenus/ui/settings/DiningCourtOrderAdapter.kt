package com.moufee.purduemenus.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.moufee.purduemenus.R
import com.moufee.purduemenus.menus.Location


class DiningCourtOrderAdapter : ListAdapter<Location, DiningCourtOrderAdapter.DiningCourtViewHolder> {


    constructor() : super(
            object : DiffUtil.ItemCallback<Location>() {
                override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
                    return oldItem.LocationId == newItem.LocationId
                }

                override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
                    return oldItem.FormalName == newItem.FormalName
                }
            })


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiningCourtViewHolder {
        return DiningCourtViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_simple_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: DiningCourtViewHolder, position: Int) {
        holder.mItemTextView.text = getItem(position).FormalName
    }

    fun getLocation(position: Int): Location {
        return getItem(position)
    }


    inner class DiningCourtViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val mItemTextView: TextView = mView.findViewById(R.id.item_title)

    }

}