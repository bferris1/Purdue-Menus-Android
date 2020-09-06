package com.moufee.purduemenus.ui.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moufee.purduemenus.databinding.FragmentMenuitemListBinding
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.repository.data.menus.MenuItem
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_menuitem_list.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

/**
 * A Fragment that contains a list of menu items for one meal at one Dining Court
 *
 *
 */
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

    @JvmField
    @Inject
    var mViewModelFactory: ViewModelProvider.Factory? = null

    @JvmField
    @Inject
    var mFavoritesRepository: FavoritesRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDiningCourtName = arguments!!.getString(ARG_DINING_COURT_NAME)
        }
    }

    override fun toggleFavorite(item: MenuItem): Boolean {
        if (mViewModel.favoriteSet.value == null) return true
        Timber.d("toggleFavorite: Adding Favorite: ${item.name} ${item.id}")
        if (!mViewModel.favoriteSet.value!!.contains(item.id)) mFavoritesRepository!!.addFavorite(item) else mFavoritesRepository!!.removeFavorite(item)
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
        menuItemRecyclerView.adapter = mDataBoundAdapter
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
        menuItemRecyclerView.layoutManager = layoutManager
    }

    private fun setListener() {
        mViewModel.selectedMenus.observe(this, { meal ->
            mDataBoundAdapter.setStations(meal[mDiningCourtName]?.stations ?: emptyList())
            meal[mDiningCourtName]?.let { "${mTimeFormatter.print(it.startTime)} - ${mTimeFormatter.print(it.endTime)}" }?.let { text -> servingTimeTextView.text = text }

        })
        /*mViewModel.getFullMenu().observe(this, fullDayMenuResource -> {
            try {
                if (fullDayMenuResource != null && fullDayMenuResource.data != null && fullDayMenuResource.data.getMenu(mDiningCourtName).isServing(mMealIndex)) {
                    mDataBoundAdapter.setStations(fullDayMenuResource.data.getMenu(mDiningCourtName).getMeal(mMealIndex).getStations());
                    mMenuItemRecyclerView.setVisibility(View.VISIBLE);
                    mNotServingTextView.setVisibility(View.GONE);
                } else {
                    mMenuItemRecyclerView.setVisibility(View.GONE);
                    mNotServingTextView.setText(fullDayMenuResource.data.getMenu(mDiningCourtName).getMeal(mMealIndex).getStatus());
                    mNotServingTextView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                // this is called frequently due to getStatus() being called on null meals
                // todo: better handling of null meals (Kotlin?)
                mDataBoundAdapter.setStations(new ArrayList<>());
                mMenuItemRecyclerView.setVisibility(View.GONE);
                mNotServingTextView.setText(R.string.not_serving);
                mNotServingTextView.setVisibility(View.VISIBLE);
            }

        });*/
// todo: refactor this and above method to remove duplicated code
/*mViewModel.getSelectedMealIndex().observe(this, integer -> {
            if (integer == null)
                return;
            mMealIndex = integer;
            Resource<FullDayMenu> fullDayMenuResource = mViewModel.getFullMenu().getValue();
            try {
                if (fullDayMenuResource != null && fullDayMenuResource.data != null && fullDayMenuResource.data.getMenu(mDiningCourtName).isServing(mMealIndex)) {
//                        DiningCourtMenu.OldMeal meal = fullDayMenuResource.data.getMenu(mDiningCourtIndex).getMeal(mMealIndex);
                    mDataBoundAdapter.setStations(mViewModel.getFullMenu().getValue().data.getMenu(mDiningCourtName).getMeal(mMealIndex).getStations());
                    mMenuItemRecyclerView.setVisibility(View.VISIBLE);
                    mNotServingTextView.setVisibility(View.GONE);
                } else {
                    mMenuItemRecyclerView.setVisibility(View.GONE);
                    mNotServingTextView.setText(fullDayMenuResource.data.getMenu(mDiningCourtName).getMeal(mMealIndex).getStatus());
                    mNotServingTextView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                mDataBoundAdapter.setStations(new ArrayList<>());
                mMenuItemRecyclerView.setVisibility(View.GONE);
                mNotServingTextView.setText(R.string.not_serving);
                mNotServingTextView.setVisibility(View.VISIBLE);
            }
        });*/
        mViewModel.favoriteSet.observe(this, { favoriteIDs: Set<String>? -> mDataBoundAdapter.setFavoriteSet(favoriteIDs) })
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mViewModel = ViewModelProviders.of(context, mViewModelFactory).get(DailyMenuViewModel::class.java)
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