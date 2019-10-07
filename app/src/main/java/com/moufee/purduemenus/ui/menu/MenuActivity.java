package com.moufee.purduemenus.ui.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.moufee.purduemenus.BuildConfig;
import com.moufee.purduemenus.R;
import com.moufee.purduemenus.api.DownloadWorker;
import com.moufee.purduemenus.databinding.ActivityMenuDatePickerTimeBinding;
import com.moufee.purduemenus.menus.Hours;
import com.moufee.purduemenus.ui.settings.CustomOrderFragmentKt;
import com.moufee.purduemenus.ui.settings.SettingsActivity;
import com.moufee.purduemenus.util.ConstantsKt;
import com.moufee.purduemenus.util.DateTimeHelper;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MenuActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static String TAG = "MENU_ACTIVITY";

    private ActivityMenuDatePickerTimeBinding mBinding;
    private MenuPagerAdapter mMenuPagerAdapter;
    private DailyMenuViewModel mViewModel;
    private NetworkReceiver mNetworkReceiver = new NetworkReceiver();
    private DateTimeFormatter mTimeFormatter = DateTimeFormat.shortTime();
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    @Inject
    SharedPreferences mSharedPreferences;
    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case SettingsActivity.KEY_PREF_SHOW_SERVING_TIMES:
                    updateServingTime();
                    break;
                case SettingsActivity.KEY_PREF_USE_NIGHT_MODE:
                    recreate();
                    break;
                case SettingsActivity.KEY_PREF_SHOW_FAVORITE_COUNT:
                    mMenuPagerAdapter.setShowFavoriteCount(mSharedPreferences.getBoolean(SettingsActivity.KEY_PREF_SHOW_FAVORITE_COUNT, true));
                    break;
                case CustomOrderFragmentKt.KEY_PREF_DINING_COURT_ORDER:
                    mViewModel.setDate(mViewModel.getCurrentDate().getValue());
            }
        }
    };

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mDispatchingAndroidInjector;
    }

    private void setListeners() {
        mViewModel.getCurrentDate().observe(this, dateTime -> {
            if (dateTime != null)
                mBinding.dateTextView.setText(DateTimeHelper.getFriendlyDateFormat(dateTime, Locale.getDefault(), getApplicationContext()));
        });
        mViewModel.getSelectedMealIndex().observe(this, newMealIndex -> {
            if (newMealIndex != null) {
                mMenuPagerAdapter.setMealIndex(newMealIndex);
                updateServingTime();
            }
        });

        mViewModel.getLocations().observe(this, locations -> mMenuPagerAdapter.setLocationList(locations));

        mViewModel.getFavoriteSet().observe(this, strings -> mMenuPagerAdapter.setFavoritesSet(strings));

        mViewModel.getFullMenu().observe(this, fullDayMenuResource -> {
            mBinding.setMenusResource(fullDayMenuResource);
            //done: loading state
            if (fullDayMenuResource != null) {
                switch (fullDayMenuResource.status) {
                    case SUCCESS:
                        mBinding.setMenu(fullDayMenuResource.data);
                        if (fullDayMenuResource.data != null)
                            mMenuPagerAdapter.setMenus(fullDayMenuResource.data.getMenuMap());
                        updateLateLunch(fullDayMenuResource.data.isLateLunchServed());
                        updateServingTime();
                        break;
                    case LOADING:
                        break;
                    case ERROR:
                        if (fullDayMenuResource.data != null) {
                            mMenuPagerAdapter.setMenus(fullDayMenuResource.data.getMenuMap());
                        } else {
                            Log.e(TAG, "setListeners: " + fullDayMenuResource.message);
                            Snackbar.make(mBinding.activityMenuCoordinatorLayout, getString(R.string.network_error_message), Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    private void updateLateLunch(boolean isLateLunchServed) {
        if (mViewModel.getSelectedMealIndex().getValue() == 2 && !isLateLunchServed) {
            mViewModel.setSelectedMealIndex(1);
        }
    }

    //todo: remove this method from activity, use Kotlin and data binding
    private void updateServingTime() {
        boolean showServingTimes = mSharedPreferences.getBoolean(SettingsActivity.KEY_PREF_SHOW_SERVING_TIMES, true);
        mBinding.setShowServingTimes(showServingTimes);

        LocalTime startTime;
        LocalTime endTime;
        String timeString;
        try {
            int diningCourtIndex = mBinding.menuViewPager.getCurrentItem();
            // todo: figure out all these null pointer warnings (convert to Kotlin and/or restructure data)
            String selectedLocation = mViewModel.getLocations().getValue().get(diningCourtIndex).getName();
            Hours hours = mViewModel.getFullMenu().getValue().data.getMenu(selectedLocation).getMeal(mViewModel.getSelectedMealIndex().getValue()).getHours();
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
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.app_name));

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu_date_picker_time);
        mBinding.setLifecycleOwner(this);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DailyMenuViewModel.class);
        mBinding.setViewModel(mViewModel);


        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

        TabLayout tabLayout = mBinding.menuTabLayout;

        Toolbar toolbar = mBinding.mainToolbar;
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mMenuPagerAdapter = new MenuPagerAdapter(fragmentManager);
        mMenuPagerAdapter.setShowFavoriteCount(mSharedPreferences.getBoolean(SettingsActivity.KEY_PREF_SHOW_FAVORITE_COUNT, true));
        mBinding.menuViewPager.setAdapter(mMenuPagerAdapter);

        tabLayout.setupWithViewPager(mBinding.menuViewPager);

        setListeners();
//        displayChangelog();

        //receive network status updates, to trigger data update when connectivity is reestablished
        //todo: integrate with Lifecycle?
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

        WorkManager workManager = WorkManager.getInstance(this);
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(DownloadWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();
        workManager.enqueueUniquePeriodicWork("downloader", ExistingPeriodicWorkPolicy.KEEP, request);
    }

    void displayChangelog() {
        int versionCode = BuildConfig.VERSION_CODE;
        boolean hasShownMessage = mSharedPreferences.getBoolean(ConstantsKt.UPDATE_MESSAGE_KEY + versionCode, false);
        if (!hasShownMessage) {
            DialogFragment changelogFragment = new ChangelogDialogFragment();
            changelogFragment.show(getSupportFragmentManager(), "changelog");
            mSharedPreferences.edit().putBoolean(ConstantsKt.UPDATE_MESSAGE_KEY + versionCode, true).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(SettingsActivity.getIntent(this));
                return true;
            case R.id.action_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@benferris.tech"}); // recipients
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
        unregisterReceiver(mNetworkReceiver);
        mBinding.menuViewPager.removeOnPageChangeListener(mOnPageChangeListener);
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
        super.onDestroy();
    }

    class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conn == null) return;
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null &&
                    networkInfo.isConnected();
            if (isConnected && mViewModel.getCurrentDate().getValue() != null) {
                //todo: better way to force update?
                mViewModel.setDate(new DateTime(mViewModel.getCurrentDate().getValue()));
            }
        }
    }
}
