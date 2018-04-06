package com.moufee.purduemenus.ui.settings


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moufee.purduemenus.R
import com.moufee.purduemenus.menus.DiningCourtComparator
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val s = "dining_court_order"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
const val KEY_PREF_DINING_COURT_ORDER = "dining_court_order"


class CustomOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
        val recyclerView: RecyclerView = view.findViewById(R.id.dining_court_order_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = mAdapter
        currentOrder = mSharedPreferences.getString(KEY_PREF_DINING_COURT_ORDER, "").split(",").toMutableList()
        if (currentOrder.size != 6)
            currentOrder = defaultOrder
        mAdapter.submitList(currentOrder)
        val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, 0) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                val fromPos = viewHolder?.adapterPosition
                val toPos = target?.adapterPosition
                if (fromPos != null && toPos != null) {
                    val item = currentOrder.removeAt(fromPos)
                    currentOrder.add(toPos, item)
//                    mAdapter.submitList(currentOrder)
                    mAdapter.notifyItemMoved(fromPos, toPos)
                    Log.d("SADF", "list $currentOrder")
                    mSharedPreferences.edit().putString(KEY_PREF_DINING_COURT_ORDER, currentOrder.joinToString(",")).apply()
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

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
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CustomOrderFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
