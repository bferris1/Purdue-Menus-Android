package com.moufee.purduemenus.ui.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moufee.purduemenus.R
import com.moufee.purduemenus.databinding.FragmentMenuitemListBinding
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * A Fragment that contains a list of menu items for one meal at one Dining Court
 *
 *
 */
@AndroidEntryPoint
class MenuItemListFragment : Fragment() {
    private var mTimeFormatter: DateTimeFormatter = DateTimeFormat.shortTime()

    // TODO: restructure so that all data stays in ViewModel (data binding?)
    var mDiningCourtName: String? = null
    private lateinit var mViewModel: MenuViewModel
    private lateinit var binding: FragmentMenuitemListBinding
    private lateinit var itemController: MenuItemController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDiningCourtName = requireArguments().getString(ARG_DINING_COURT_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuitemListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemController = MenuItemController(object : MenuItemController.AdapterCallbacks {
            override fun onItemLongPressed(item: MenuItemViewObject): Boolean {
                return true
            }
        })
        binding.menuItemRecyclerView.adapter = itemController.adapter
        binding.menuItemRecyclerView.itemAnimator = null
        val layoutManager: RecyclerView.LayoutManager =
            if (context?.resources?.configuration?.screenWidthDp ?: 0 > 500) {
                GridLayoutManager(context, 2).apply {
                    itemController.spanCount = 2
                    spanSizeLookup = itemController.spanSizeLookup
                }
            } else LinearLayoutManager(context)
        binding.menuItemRecyclerView.layoutManager = layoutManager
    }

    private fun setListener() {
        mViewModel.selectedMenus.observe(this, { menus: Map<String, DiningCourtMeal> ->
            val menu = menus[mDiningCourtName]
            val stations = menu?.stations ?: emptyList()
            val items = stations.flatMap { station ->
                listOf(HeaderItemViewObject(station.name)).plus(station.items.map {
                    MenuItemViewObject(
                        it.id,
                        it.name,
                        it.isVegetarian,
                        false
                    )
                })
            }
            itemController.setData(items)
            binding.dataAvailable = stations.isNotEmpty()
            binding.notServingTextview.text =
                if (menu?.status != null && menu.status != "Open") menu.status else getString(R.string.no_data)
            menu?.let {
                if (it.startTime != null && it.endTime != null)
                    "${mTimeFormatter.print(it.startTime)} - ${mTimeFormatter.print(it.endTime)}"
                else ""
            }?.let { text -> binding.servingTimeTextView.text = text }

        })
//        lifecycleScope.launchWhenStarted {
//            mViewModel.favoriteSet.collect { favoriteIDs: Set<String> -> mDataBoundAdapter.setFavoriteSet(favoriteIDs) }
//        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mViewModel = ViewModelProvider(context).get(MenuViewModel::class.java)
            setListener()
        }
    }

    companion object {
        private const val ARG_DINING_COURT_NAME = "dining-court-name"
        fun newInstance(diningCourtName: String): MenuItemListFragment {
            val fragment = MenuItemListFragment()
            val args = Bundle()
            args.putString(ARG_DINING_COURT_NAME, diningCourtName)
            fragment.arguments = args
            return fragment
        }
    }
}