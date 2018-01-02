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
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.moufee.purduemenus.MenusApp;
import com.moufee.purduemenus.R;
import com.moufee.purduemenus.databinding.ActivityMenuDatePickerTimeBinding;
import com.moufee.purduemenus.menus.DailyMenuViewModel;
import com.moufee.purduemenus.menus.DiningCourtMenu;
import com.moufee.purduemenus.menus.FullDayMenu;
import com.moufee.purduemenus.ui.login.LoginActivity;
import com.moufee.purduemenus.ui.settings.SettingsActivity;
import com.moufee.purduemenus.ui.settings.SettingsFragment;
import com.moufee.purduemenus.util.DateTimeHelper;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import javax.inject.Inject;

public class MenuActivity extends AppCompatActivity implements MenuItemListFragment.OnListFragmentInteractionListener {

    private static String TAG = "MENU_ACTIVITY";

    private ActivityMenuDatePickerTimeBinding mBinding;
    private MenuPagerAdapter mMenuPagerAdapter;
    private DailyMenuViewModel mViewModel;
    private NetworkReceiver mNetworkReceiver = new NetworkReceiver();
    private DateTimeFormatter mTimeFormatter = DateTimeFormat.shortTime();
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    @Inject SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SettingsActivity.KEY_PREF_SHOW_SERVING_TIMES))
                updateServingTime();
            else if (key.equals(SettingsActivity.KEY_PREF_USE_NIGHT_MODE))
                recreate();
        }
    };


    @Override
    public void onListFragmentInteraction(com.moufee.purduemenus.menus.MenuItem item) {

    }


    private void setListeners(){
        mViewModel.getCurrentDate().observe(this, new Observer<DateTime>() {
            @Override
            public void onChanged(@Nullable DateTime dateTime) {
                if (dateTime != null)
                    mBinding.dateTextView.setText(DateTimeHelper.getFriendlyDateFormat(dateTime, Locale.getDefault(), getApplicationContext()));
            }
        });
        mViewModel.getSelectedMealIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer newMealIndex) {
                if (newMealIndex != null) {
                    mBinding.setSelectedMealIndex(newMealIndex);
                    mMenuPagerAdapter.setMealIndex(newMealIndex);
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
                            mBinding.setMenu(fullDayMenuResource.data);
                            if (fullDayMenuResource.data != null)
                                mMenuPagerAdapter.setMenus(fullDayMenuResource.data.getMenus());
                            updateLateLunch(fullDayMenuResource.data.isLateLunchServed());
                            updateServingTime();
                            break;
                        case LOADING:
                            break;
                        case ERROR:
                            if (fullDayMenuResource.data != null){
                                mMenuPagerAdapter.setMenus(fullDayMenuResource.data.getMenus());
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

    private void updateLateLunch(boolean isLateLunchServed){
            if (mViewModel.getSelectedMealIndex().getValue() == 2 && !isLateLunchServed) {
                mViewModel.setSelectedMealIndex(1);
        }
    }

    private void updateServingTime(){
        boolean showServingTimes = mSharedPreferences.getBoolean(SettingsActivity.KEY_PREF_SHOW_SERVING_TIMES,true);
        mBinding.setShowServingTimes(showServingTimes);

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
            mBinding.mealTimeTextView.setText("");
            return;
        }
        mBinding.mealTimeTextView.setText(timeString);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: oncreate called");
        super.onCreate(savedInstanceState);
        MenusApp app = (MenusApp) getApplication();
        app.getAppComponent().inject(this);
        setTitle(getString(R.string.app_name));

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu_date_picker_time);
        mBinding.setSelectedMealIndex(0);

        mViewModel = ViewModelProviders.of(this).get(DailyMenuViewModel.class);
        mViewModel.setDate(new DateTime());
        mViewModel.setSelectedMealIndex(DateTimeHelper.getCurrentMealIndex());
        mBinding.setViewModel(mViewModel);


        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

        TabLayout tabLayout = mBinding.menuTabLayout;

        Toolbar toolbar = mBinding.mainToolbar;
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mMenuPagerAdapter = new MenuPagerAdapter(fragmentManager);
        mBinding.menuViewPager.setAdapter(mMenuPagerAdapter);

        tabLayout.setupWithViewPager(mBinding.menuViewPager);

        setListeners();

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
            if (conn == null) return;
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
