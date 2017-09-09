package com.moufee.purduemenus.ui.menu;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.databinding.ActivityMenuDatePickerTimeBinding;
import com.moufee.purduemenus.menus.DailyMenuViewModel;
import com.moufee.purduemenus.menus.DiningCourtMenu;
import com.moufee.purduemenus.menus.FullDayMenu;
import com.moufee.purduemenus.ui.login.LoginActivity;
import com.moufee.purduemenus.ui.settings.SettingsActivity;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class MenuActivity extends AppCompatActivity implements LifecycleRegistryOwner, MenuItemListFragment.OnListFragmentInteractionListener {

//    @BindView(R.id.menu_view_pager) ViewPager mViewPager;
    private FullDayMenu mFullDayMenu;
    private static String TAG = "MENU_ACTIVITY";

    private ActivityMenuDatePickerTimeBinding mBinding;
    private DailyMenuViewModel mViewModel;
    private NetworkReceiver mNetworkReceiver = new NetworkReceiver();
    private DateTimeFormatter mTimeFormatter = DateTimeFormat.shortTime();
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SettingsActivity.KEY_PREF_SHOW_SERVING_TIMES))
            updateServingTime();
        }
    };

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);



    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    @Override
    public void onListFragmentInteraction(com.moufee.purduemenus.menus.MenuItem item) {

    }

    //todo: locale order? and possible efficiency improvements/streamlining
    private String getFriendlyDateFormat(DateTime dateTime){
        String pattern;
        final int HINT_START_HOUR = 22; // the hour at which day hints will be shown (e.g. after 10pm)
        final int HINT_END_HOUR = 4; //the hour in the morning after which hints will not be displayed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(),"EEEE MMMMM dd");
        } else pattern = "EEEE MMMMM dd";
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern).withLocale(Locale.getDefault());
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern(" (E)").withLocale(Locale.getDefault());
        DateTime now = new DateTime();
        boolean showDayHints = now.getHourOfDay() >= HINT_START_HOUR || now.getHourOfDay() <= HINT_END_HOUR;
        String dayString = showDayHints ? dayFormat.print(now) : "";
        Interval today = new Interval(now.withTimeAtStartOfDay(),now.plusDays(1).withTimeAtStartOfDay());
        if (today.contains(dateTime))
            return getString(R.string.today) + dayString;
        Interval tomorrow = new Interval(now.plusDays(1).withTimeAtStartOfDay(), now.plusDays(2).withTimeAtStartOfDay());
        if (tomorrow.contains(dateTime)) {
            dayString = showDayHints ? dayFormat.print(now.plusDays(1)) : "";
            return getString(R.string.tomorrow) + dayString;
        }
        Interval yesterday = new Interval(now.plusDays(-1).withTimeAtStartOfDay(), now.withTimeAtStartOfDay());
        if (yesterday.contains(dateTime)) {
            dayString = showDayHints ? dayFormat.print(now.plusDays(-1)) : "";
            return getString(R.string.yesterday) + dayString;
        }
        return format.print(dateTime);
    }

    private void setListeners(){
        mViewModel.getCurrentDate().observe(this, new Observer<DateTime>() {
            @Override
            public void onChanged(@Nullable DateTime dateTime) {
                if (dateTime != null)
                    mBinding.dateTextView.setText(getFriendlyDateFormat(dateTime));
            }
        });
        mViewModel.getSelectedMealIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    mBinding.setSelectedMealIndex(integer);
                    mBinding.menuViewPager.getAdapter().notifyDataSetChanged();
                    updateServingTime();
                }
            }
        });

        mViewModel.getFullMenu().observe(this, new Observer<Resource<FullDayMenu>>() {
            @Override
            public void onChanged(@Nullable Resource<FullDayMenu> fullDayMenuResource) {
                mBinding.setMenusResource(fullDayMenuResource == null ? null: fullDayMenuResource);
                //done: loading state
                if (fullDayMenuResource != null){
                    switch (fullDayMenuResource.status){
                        case SUCCESS:
                            mFullDayMenu = fullDayMenuResource.data;
                            mBinding.setMenu(fullDayMenuResource.data);
                            mBinding.menuViewPager.getAdapter().notifyDataSetChanged();
                            updateLateLunch();
                            updateServingTime();
//                            mBinding.loadingIndicatorView.getRootView().setVisibility(View.GONE);
//                            mViewPager.setVisibility(View.VISIBLE);
                            break;
                        case LOADING:
//                            mViewPager.setVisibility(View.INVISIBLE);
//                            mBinding.loadingIndicatorView.getRootView().setVisibility(View.VISIBLE);
                            break;
                        case ERROR:
//                            mBinding.loadingIndicatorView.getRootView().setVisibility(View.GONE);
                            if (fullDayMenuResource.data != null){
//                                mViewPager.setVisibility(View.VISIBLE);
                                mBinding.menuViewPager.getAdapter().notifyDataSetChanged();
                            } else {
                                Snackbar.make(mBinding.activityMenuCoordinatorLayout, getString(R.string.network_error_message), Snackbar.LENGTH_SHORT).show();
                                mBinding.menuViewPager.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            }
        });
    }

    private void updateLateLunch(){
            if (!mFullDayMenu.isLateLunchServed() && mViewModel.getSelectedMealIndex().getValue() == 2) {
                mViewModel.setSelectedMealIndex(1);
        }
    }

    /**
     * Initializes the UI to default values based on current time
     */
    private int getCurrentMealIndex(){
        DateTime now = new DateTime();
        if (now.getHourOfDay() <= 9)
            return 0;
        else if (now.getHourOfDay() <= 13)
            return 1;
        else if (now.getHourOfDay() <= 16)
            return 2;
        else if (now.getHourOfDay() <= 21 )
            return 3;
        else
            return 0;
    }

    private void updateServingTime(){
        if (!mSharedPreferences.getBoolean(SettingsActivity.KEY_PREF_SHOW_SERVING_TIMES,true)){
            mBinding.mealTimeTextView.setVisibility(View.GONE);
            return;
        }
        LocalTime startTime;
        LocalTime endTime;
        String timeString;
        try {
            int diningCourtIndex = mBinding.menuViewPager.getCurrentItem();
            DiningCourtMenu.Hours hours = mViewModel.getFullMenu().getValue().data.getMenu(diningCourtIndex).getMeal(mViewModel.getSelectedMealIndex().getValue()).getHours();
            startTime = hours.getStartTime();
            endTime = hours.getEndTime();
            timeString = mTimeFormatter.print(startTime) + " - " + mTimeFormatter.print(endTime);
        } catch (Exception e) {
//            e.printStackTrace();
            mBinding.mealTimeTextView.setText("");
            mBinding.mealTimeTextView.setVisibility(View.GONE);
            return;
        }
        mBinding.mealTimeTextView.setText(timeString);
        mBinding.mealTimeTextView.setVisibility(View.VISIBLE);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: oncreate called");
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu_date_picker_time);
        mBinding.setSelectedMealIndex(0);

        mViewModel = ViewModelProviders.of(this).get(DailyMenuViewModel.class);
        mViewModel.init(new DateTime(), getCurrentMealIndex());
        mBinding.setViewModel(mViewModel);
        setListeners();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

        TabLayout tabLayout = (TabLayout) mBinding.menuTabLayout;

        Toolbar toolbar = (Toolbar) mBinding.mainToolbar;
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mBinding.menuViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                int mealIndex = mViewModel.getSelectedMealIndex().getValue();
                if (mFullDayMenu != null && position < mFullDayMenu.getNumMenus() && mFullDayMenu.getMenu(position).isServing(mealIndex))
                    return MenuItemListFragment.newInstance(position, mealIndex);
                if (mFullDayMenu != null && position < mFullDayMenu.getNumMenus() && mFullDayMenu.getMenu(position).getMeal(mealIndex) != null)
                    return NotServingFragment.newInstance(mFullDayMenu.getMenu(position).getMeal(mealIndex).getStatus());
                return NotServingFragment.newInstance(getString(R.string.not_serving));
            }

            @Override
            public int getCount() {
                if (mFullDayMenu != null)
                    return mFullDayMenu.getNumMenus();
                return 0;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFullDayMenu.getMenu(position).getLocation();
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        });

        tabLayout.setupWithViewPager(mBinding.menuViewPager);

        //receive network status updates, to trigger data update when connectivity is reestablished
        //todo: integrate with Lifecycle
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateServingTime();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mBinding.menuViewPager.addOnPageChangeListener(mOnPageChangeListener);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
            Log.d(TAG, "onResume: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(SettingsActivity.getIntent(this));
                return true;
            case R.id.action_login:
                Intent loginIntent = new Intent(this,LoginActivity.class);
                startActivity(loginIntent);
                return true;
            case R.id.action_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"feedback@purdue.tools"}); // recipients
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Purdue Menus Feedback");
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        unregisterReceiver(mNetworkReceiver);
        mBinding.menuViewPager.removeOnPageChangeListener(mOnPageChangeListener);
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
        super.onDestroy();
    }

    class NetworkReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn =  (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null &&
                    networkInfo.isConnected();
            if (isConnected && mViewModel.getCurrentDate().getValue() != null){
                //todo: better way to force update?
                mViewModel.setDate(new DateTime(mViewModel.getCurrentDate().getValue()));
            }
        }
    }
}
