package com.moufee.purduemenus.ui.settings


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.moufee.purduemenus.R
import com.moufee.purduemenus.menus.DiningCourtComparator
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [CustomOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
const val KEY_PREF_DINING_COURT_ORDER = "dining_court_order"


class CustomOrderFragment : androidx.fragment.app.Fragment() {
    private lateinit var mAdapter: DiningCourtOrderAdapter
    private val defaultOrder = DiningCourtComparator.diningCourts
    private var currentOrder: MutableList<String> = ArrayList(defaultOrder)

    @Inject
    lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_custom_order, container, false)
        mAdapter = DiningCourtOrderAdapter()
        val recyclerView: androidx.recyclerview.widget.RecyclerView = view.findViewById(R.id.dining_court_order_recyclerview)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recyclerView.adapter = mAdapter
        currentOrder = mSharedPreferences.getString(KEY_PREF_DINING_COURT_ORDER, "").split(",").toMutableList()
        if (currentOrder.size != 5)
            currentOrder = defaultOrder
        mAdapter.submitList(currentOrder)
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, 0) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                val item = currentOrder.removeAt(fromPos)
                currentOrder.add(toPos, item)
//                    mAdapter.submitList(currentOrder)
                mAdapter.notifyItemMoved(fromPos, toPos)
                Log.d("SADF", "list $currentOrder")
                mSharedPreferences.edit().putString(KEY_PREF_DINING_COURT_ORDER, currentOrder.joinToString(",")).apply()
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

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
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
