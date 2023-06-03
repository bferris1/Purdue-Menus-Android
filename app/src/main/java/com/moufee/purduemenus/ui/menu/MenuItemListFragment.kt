package com.moufee.purduemenus.ui.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moufee.purduemenus.R
import com.moufee.purduemenus.databinding.FragmentMenuitemListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A Fragment that contains a list of menu items for one meal at one Dining Court
 *
 *
 */
@AndroidEntryPoint
class MenuItemListFragment : Fragment() {

    // TODO: restructure so that all data stays in ViewModel (data binding?)
    lateinit var mDiningCourtName: String
    private lateinit var mViewModel: MenuViewModel
    private lateinit var binding: FragmentMenuitemListBinding
    private lateinit var itemController: MenuItemController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDiningCourtName = requireArguments().getString(ARG_DINING_COURT_NAME, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMenuitemListBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemController = MenuItemController(object : MenuItemController.AdapterCallbacks {
            override fun onItemLongPressed(item: MenuItemViewObject): Boolean {
                mViewModel.toggleFavorite(item.menuItem)
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
        setListener()
    }

    private fun setListener() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.getMenuDetailsUiState(mDiningCourtName).collect { state ->
                    itemController.setData(state.items)
                    binding.dataAvailable = state.items.isNotEmpty()
                    binding.notServingTextview.text =
                        if (state.status != null && state.status != "Open") state.status else getString(R.string.no_data)
                    binding.servingTimeTextView.text = state.servingTimeText
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mViewModel = ViewModelProvider(context).get(MenuViewModel::class.java)
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