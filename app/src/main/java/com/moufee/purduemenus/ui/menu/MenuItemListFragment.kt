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
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moufee.purduemenus.R
import com.moufee.purduemenus.databinding.FragmentMenuitemListBinding
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.repository.data.menus.MenuItem
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

/**
 * A Fragment that contains a list of menu items for one meal at one Dining Court
 *
 *
 */
@AndroidEntryPoint
class MenuItemListFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment(), OnToggleFavoriteListener {
    private var mTimeFormatter: DateTimeFormatter = DateTimeFormat.shortTime()

    // TODO: restructure so that all data stays in ViewModel (data binding?)
    var mDiningCourtName: String? = null
    private var mDataBoundAdapter: MenuRecyclerViewAdapter = MenuRecyclerViewAdapter((this))
    private lateinit var mViewModel: DailyMenuViewModel
    private lateinit var binding: FragmentMenuitemListBinding

    @Inject
    lateinit var mFavoritesRepository: FavoritesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDiningCourtName = requireArguments().getString(ARG_DINING_COURT_NAME)
        }
    }

    override fun toggleFavorite(item: MenuItem): Boolean {
        if (mViewModel.favoriteSet.value == null) return true
        Timber.d("toggleFavorite: Adding Favorite: ${item.name} ${item.id}")
        if (!mViewModel.favoriteSet.value!!.contains(item.id)) mFavoritesRepository.addFavorite(item) else mFavoritesRepository.removeFavorite(item)
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMenuitemListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.menuItemRecyclerView.adapter = mDataBoundAdapter
        val layoutManager: RecyclerView.LayoutManager =
                if (context?.resources?.configuration?.screenWidthDp ?: 0 > 500) {
                    GridLayoutManager(context, 2).apply {
                        spanSizeLookup = object : SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (mDataBoundAdapter.isHeader(position)) 2 else 1
                            }
                        }
                    }
                } else LinearLayoutManager(context)
        binding.menuItemRecyclerView.layoutManager = layoutManager
    }

    private fun setListener() {
        mViewModel.selectedMenus.observe(this, { menus: Map<String, DiningCourtMeal> ->
            val menu = menus[mDiningCourtName]
            val stations = menu?.stations ?: emptyList()
            mDataBoundAdapter.setStations(stations)
            binding.dataAvailable = stations.isNotEmpty()
            binding.notServingTextview.text = if (menu?.status != null && menu.status != "Open") menu.status else getString(R.string.no_data)
            menu?.let {
                if (it.startTime != null && it.endTime != null)
                    "${mTimeFormatter.print(it.startTime)} - ${mTimeFormatter.print(it.endTime)}"
                else ""
            }?.let { text -> binding.servingTimeTextView.text = text }

        })
        mViewModel.favoriteSet.observe(this, { favoriteIDs: Set<String> -> mDataBoundAdapter.setFavoriteSet(favoriteIDs) })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mViewModel = ViewModelProvider(context).get(DailyMenuViewModel::class.java)
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