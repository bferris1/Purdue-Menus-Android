package com.moufee.purduemenus.ui.menu;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.moufee.purduemenus.R;
import com.moufee.purduemenus.menus.DailyMenuViewModel;
import com.moufee.purduemenus.menus.FullDayMenu;
import com.moufee.purduemenus.menus.MenuItem;
import com.moufee.purduemenus.menus.MenuRecyclerViewAdapter;
import com.moufee.purduemenus.menus.OnToggleFavoriteListener;
import com.moufee.purduemenus.repository.FavoritesRepository;
import com.moufee.purduemenus.util.Resource;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;

/**
 * A Fragment that contains a list of menu items for one Meal at one Dining Court
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MenuItemListFragment extends Fragment implements OnToggleFavoriteListener {

    private static final String ARG_DINING_COURT_INDEX = "dining-court-index";
    private static final String ARG_MEAL_INDEX = "meal-index";
    private static final String TAG = "MENU_ITEM_LIST_FRAGMENT";

    // TODO: restructure so that all data stays in ViewModel (data binding?)
    private int mDiningCourtIndex = 0;
    private int mMealIndex = 0;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mMenuItemRecyclerView;
    private TextView mNotServingTextView;
    private MenuRecyclerViewAdapter mDataBoundAdapter;
    private DailyMenuViewModel mViewModel;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    FavoritesRepository mFavoritesRepository;
    @Inject
    FirebaseAnalytics mFirebaseAnalytics;

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
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBoundAdapter = new MenuRecyclerViewAdapter(this);

        if (getArguments() != null) {
            mDiningCourtIndex = getArguments().getInt(ARG_DINING_COURT_INDEX);
            mMealIndex = getArguments().getInt(ARG_MEAL_INDEX);
        }
    }

    @Override
    public boolean toggleFavorite(MenuItem item) {
        if (mViewModel.getFavoriteSet().getValue() == null) return true;
        Log.d(TAG, "toggleFavorite: Adding Favorite " + item.getName() + " " + item.getId());
        mFirebaseAnalytics.logEvent("toggle_favorite", null);
        if (!mViewModel.getFavoriteSet().getValue().contains(item.getId()))
            mFavoritesRepository.addFavorite(item);
        else
            mFavoritesRepository.removeFavorite(item);
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menuitem_list, container, false);


        // Set the adapter
        Context context = view.getContext();
        mMenuItemRecyclerView = view.findViewById(R.id.menu_item_recyclerview);
        mNotServingTextView = view.findViewById(R.id.not_serving_textview);
        mMenuItemRecyclerView.setAdapter(mDataBoundAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        if (context.getResources().getConfiguration().screenWidthDp > 500) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            layoutManager = gridLayoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return mDataBoundAdapter.isHeader(position) ? 2 : 1;
                }
            });
        }
        mMenuItemRecyclerView.setLayoutManager(layoutManager);

        return view;
    }


    private void setListener() {
        mViewModel.getFullMenu().observe(this, fullDayMenuResource -> {
            try {
                if (fullDayMenuResource != null && fullDayMenuResource.data != null && fullDayMenuResource.data.getMenu(mDiningCourtIndex).isServing(mMealIndex)) {
                    mDataBoundAdapter.setStations(fullDayMenuResource.data.getMenu(mDiningCourtIndex).getMeal(mMealIndex).getStations());
                    mMenuItemRecyclerView.setVisibility(View.VISIBLE);
                    mNotServingTextView.setVisibility(View.GONE);
                } else {
                    mMenuItemRecyclerView.setVisibility(View.GONE);
                    mNotServingTextView.setText(fullDayMenuResource.data.getMenu(mDiningCourtIndex).getMeal(mMealIndex).getStatus());
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

        });
        // todo: refactor this and above method to remove duplicated code
        mViewModel.getSelectedMealIndex().observe(this, integer -> {
            if (integer == null)
                return;
            mMealIndex = integer;
            Resource<FullDayMenu> fullDayMenuResource = mViewModel.getFullMenu().getValue();
            try {
                if (fullDayMenuResource != null && fullDayMenuResource.data != null && fullDayMenuResource.data.getMenu(mDiningCourtIndex).isServing(mMealIndex)) {
//                        DiningCourtMenu.Meal meal = fullDayMenuResource.data.getMenu(mDiningCourtIndex).getMeal(mMealIndex);
                    mDataBoundAdapter.setStations(mViewModel.getFullMenu().getValue().data.getMenu(mDiningCourtIndex).getMeal(mMealIndex).getStations());
                    mMenuItemRecyclerView.setVisibility(View.VISIBLE);
                    mNotServingTextView.setVisibility(View.GONE);
                } else {
                    mMenuItemRecyclerView.setVisibility(View.GONE);
                    mNotServingTextView.setText(fullDayMenuResource.data.getMenu(mDiningCourtIndex).getMeal(mMealIndex).getStatus());
                    mNotServingTextView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                mDataBoundAdapter.setStations(new ArrayList<>());
                mMenuItemRecyclerView.setVisibility(View.GONE);
                mNotServingTextView.setText(R.string.not_serving);
                mNotServingTextView.setVisibility(View.VISIBLE);
            }
        });
        mViewModel.getFavoriteSet().observe(this, favoriteIDs -> mDataBoundAdapter.setFavoriteSet(favoriteIDs));
    }


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        if (context instanceof AppCompatActivity) {
            mViewModel = ViewModelProviders.of((AppCompatActivity) context, mViewModelFactory).get(DailyMenuViewModel.class);
            setListener();
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
}
