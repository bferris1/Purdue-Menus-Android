package com.moufee.purduemenus.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.moufee.purduemenus.databinding.FragmentDiningCourtListItemBinding
import com.moufee.purduemenus.repository.data.menus.Location


class DiningCourtOrderAdapter(val listener: OnLocationChangedListener) : ListAdapter<Location, DiningCourtOrderAdapter.DiningCourtViewHolder>(
        object : DiffUtil.ItemCallback<Location>() {
            override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
                return oldItem.LocationId == newItem.LocationId
            }

            override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
                return oldItem.FormalName == newItem.FormalName && oldItem.isHidden == newItem.isHidden
            }
        }) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiningCourtViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentDiningCourtListItemBinding.inflate(layoutInflater, parent, false)
        return DiningCourtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiningCourtViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class DiningCourtViewHolder(var binding: FragmentDiningCourtListItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location) {
            binding.location = location
            binding.listener = listener
            binding.executePendingBindings()
        }

    }

    interface OnLocationChangedListener {
        fun onLocationVisibilityChanged(location: Location)
    }

}