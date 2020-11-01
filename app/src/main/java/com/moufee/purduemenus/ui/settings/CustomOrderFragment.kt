package com.moufee.purduemenus.ui.settings


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.moufee.purduemenus.R
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.repository.data.menus.Location
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [CustomOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class CustomOrderFragment : androidx.fragment.app.Fragment() {
    private lateinit var mAdapter: DiningCourtOrderAdapter

    @Inject
    lateinit var mSharedPreferences: SharedPreferences

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    lateinit var mViewModel: LocationSettingsViewModel

    @Inject
    lateinit var mMenuRepository: MenuRepository


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_custom_order, container, false)
        mAdapter = DiningCourtOrderAdapter(listener = object : DiningCourtOrderAdapter.OnLocationChangedListener {
            override fun onLocationVisibilityChanged(location: Location) {
                location.isHidden = !location.isHidden
                mAdapter.notifyDataSetChanged()
                mMenuRepository.updateLocations(location)
            }
        })
        val recyclerView: androidx.recyclerview.widget.RecyclerView = view.findViewById(R.id.dining_court_order_recyclerview)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recyclerView.adapter = mAdapter
        if (mViewModel.orderedLocations.size > 0) {
            mAdapter.submitList(mViewModel.orderedLocations)
        }
        mViewModel.locations.observe(viewLifecycleOwner, {
            Timber.d(it.toString())
            // initialize once: after this, orderedLocations is the single source of truth for the location info
            // if we update it every time the liveData changes, updates may happen out of order with respect to UI changes, resulting in bad data being saved to DB
            // maybe there is a better way to handle this?
            if (mViewModel.orderedLocations.size == 0) {
                mAdapter.submitList(it)
                mViewModel.orderedLocations = it as MutableList<Location>
            }
        })
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, 0) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                mViewModel.orderedLocations.add(toPos, mViewModel.orderedLocations.removeAt(fromPos))
                mAdapter.notifyItemMoved(fromPos, toPos)
                Timber.d("from $fromPos to $toPos")
                return true
            }

            override fun onSelectedChanged(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder?.itemView != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(viewHolder.itemView)
                    ViewCompat.setElevation(viewHolder.itemView, 10f)
                }
            }

            override fun clearView(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                for ((i, location) in mViewModel.orderedLocations.withIndex()) {
                    location.displayOrder = i
                }
                mMenuRepository.updateLocations(mViewModel.orderedLocations)
                ViewCompat.setElevation(viewHolder.itemView, 0f)
            }

            override fun onChildDraw(c: Canvas, recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, false)
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {

            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
        return view
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        if (context is FragmentActivity) {
            mViewModel = ViewModelProvider(context, mViewModelFactory).get((LocationSettingsViewModel::class.java))
        }
        super.onAttach(context)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment CustomOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = CustomOrderFragment()
    }
}
