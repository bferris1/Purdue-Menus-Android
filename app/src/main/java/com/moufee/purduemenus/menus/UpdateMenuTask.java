package com.moufee.purduemenus.menus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.moufee.purduemenus.api.MenuCache;
import com.moufee.purduemenus.api.MenuDownloader;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by Ben on 03/08/2017.
 * A task to update LiveData and cache
 */

//todo: potentially move this functionality to MenusRepository
public class UpdateMenuTask implements Runnable {

    private MutableLiveData<Resource<FullDayMenu>> mFullMenu;
    private DateTime mMenuDate;
    private static final String TAG = "UpdateMenuTask";
    private boolean mFetchedFromFile = false;
    private MenuDownloader mMenuDownloader;
    private ConnectivityManager mConnectivityManager;
    private List<Location> mLocationList;
    private MenuCache mMenuCache;

    @Inject
    public UpdateMenuTask(Context context, MenuDownloader menuDownloader, MenuCache menuCache) {
        mMenuDownloader = menuDownloader;
        mMenuCache = menuCache;
        mMenuDate = new DateTime();
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public UpdateMenuTask forDate(DateTime date) {
        this.mMenuDate = date;
        return this;
    }

    public UpdateMenuTask forLocations(List<Location> locations) {
        this.mLocationList = locations;
        return this;
    }

    public UpdateMenuTask setLiveData(MutableLiveData<Resource<FullDayMenu>> liveData) {
        this.mFullMenu = liveData;
        return this;
    }

    @Override
    public void run() {
        if (mLocationList == null || mFullMenu == null) {
            return;
        }
        if (mLocationList.size() == 0) {
            return;
        }
        // we don't post loading status unless it's a cache miss
        FullDayMenu fileMenus = mMenuCache.get(mMenuDate);
        if (fileMenus != null) {
            mFetchedFromFile = true;
            mFullMenu.postValue(Resource.success(fileMenus));
            Log.d(TAG, "getFullMenu: Read from file!");
        } else {
            mFullMenu.postValue(Resource.loading(null));
        }
        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            if (mFetchedFromFile)
                return;
            else
                mFullMenu.postValue(Resource.error("Not connected to network", null));
            return;
        }
        Log.d(TAG, "fetchFromNetwork: " + mLocationList);

        //todo: figure out if this needs to  be retained
        //I'm thinking this can be ignored since we still want to cache even if it won't be displayed
        Disposable disposable = mMenuDownloader.getMenus(mMenuDate, mLocationList).subscribe((fullDayMenu, throwable) -> {
            if (throwable != null) {
                if (mFullMenu.getValue() != null)
                    mFullMenu.postValue(Resource.error("Network Error", mFullMenu.getValue().data));
                else
                    mFullMenu.postValue(Resource.error(throwable.getMessage(), null));
                return;
            }
            mFullMenu.postValue(Resource.success(fullDayMenu));
            try {
                mMenuCache.put(fullDayMenu);
            } catch (IOException e) {
                Log.e(TAG, "onResponse: error saving to file ", e);
                mFullMenu.postValue(Resource.error(e.getMessage() != null ? e.getMessage() : "An error occurred while saving to file.", null));
            }
        });

    }
}
