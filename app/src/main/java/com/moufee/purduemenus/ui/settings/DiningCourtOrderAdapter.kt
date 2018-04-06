package com.moufee.purduemenus.ui.settings

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.moufee.purduemenus.R


class DiningCourtOrderAdapter : ListAdapter<String, DiningCourtOrderAdapter.DiningCourtViewHolder> {


    constructor() : super(
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }
            })


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiningCourtViewHolder {
        return DiningCourtViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_simple_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: DiningCourtViewHolder, position: Int) {
        holder.mItemTextView.text = getItem(position)
    }


    inner class DiningCourtViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mItemTextView: TextView = mView.findViewById(R.id.item_title)

    }

}