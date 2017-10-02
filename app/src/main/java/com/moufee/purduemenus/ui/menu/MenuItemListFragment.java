package com.moufee.purduemenus.ui.menu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.menus.DailyMenuViewModel;
import com.moufee.purduemenus.menus.DiningCourtMenu;
import com.moufee.purduemenus.menus.MenuItem;
import com.moufee.purduemenus.menus.MenuRecyclerViewAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * A Fragment that contains a list of menu items for one Meal at one Dining Court
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MenuItemListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_DINING_COURT_INDEX = "dining-court-index";
    private static final String ARG_MEAL_INDEX = "meal-index";
    private static final String TAG = "MENU_ITEM_LIST_FRAGMENT";

    // TODO: restructure so that all data stays in ViewModel (data binding?)
    private int mDiningCourtIndex = 0;
    private int mMealIndex = 0;
    private List<DiningCourtMenu.Station> mStations;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mMenuItemRecyclerView;
//    private MenuItemAdapter mAdapter;
//    private SectionedRecyclerViewAdapter mSectionAdapter;
    private MenuRecyclerViewAdapter mDataBoundAdapter;
    private DailyMenuViewModel mViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MenuItemListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MenuItemListFragment newInstance(int diningCourtIndex, int mealIndex) {
        MenuItemListFragment fragment = new MenuItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DINING_COURT_INDEX, diningCourtIndex);
        args.putInt(ARG_MEAL_INDEX, mealIndex);
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDiningCourtIndex = getArguments().getInt(ARG_DINING_COURT_INDEX);
            mMealIndex = getArguments().getInt(ARG_MEAL_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menuitem_list, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(DailyMenuViewModel.class);

        //todo: remove this?
        if (mViewModel.getFullMenu() == null)
        mViewModel.init(new DateTime(), 0);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mMenuItemRecyclerView = (RecyclerView) view.findViewById(R.id.menu_item_recyclerview);

            mMenuItemRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            updateUI();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name

        void onListFragmentInteraction(MenuItem item);
    }

    private void updateUI(){
        List<DiningCourtMenu.Station> stations;
        try {
//            mStations = mViewModel.getFullMenu().getValue().getMenu(mDiningCourtIndex).getMeal(mMealIndex).getStations();
            mStations = mViewModel.getFullMenu().getValue().data.getMenu(mDiningCourtIndex).getMeal(mMealIndex).getStations();
        } catch (Exception e) {
            Log.e(TAG, "updateUI: error",e );
            //todo: handle more gracefully?
            mStations = new ArrayList<>();
        }
        if (mDataBoundAdapter == null){
            mDataBoundAdapter = new MenuRecyclerViewAdapter();
            mDataBoundAdapter.setStations(mStations);
            mMenuItemRecyclerView.setAdapter(mDataBoundAdapter);
        }else {
//            mAdapter.setStations(stations);
            mDataBoundAdapter.notifyDataSetChanged();
        }

    }
}
