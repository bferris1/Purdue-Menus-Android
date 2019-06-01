package com.moufee.purduemenus.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.db.LocationDao;
import com.moufee.purduemenus.menus.FullDayMenu;
import com.moufee.purduemenus.menus.Location;
import com.moufee.purduemenus.menus.LocationsResponse;
import com.moufee.purduemenus.menus.UpdateMenuTask;
import com.moufee.purduemenus.util.AppExecutors;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;


/**
 * Created by Ben on 22/07/2017.
 * Where all the menus live
 * Abstracts the data sources from the rest of the app
 * Creates threads to retrieve and process menu data
 */
@Singleton
public class MenuRepository {

    private static final String TAG = "MenuRepository";

    private Webservice mWebservice;
    private AppExecutors mAppExecutors;
    private LocationDao mLocationDao;
    private Provider<UpdateMenuTask> mMenuTaskProvider;

    @Inject
    public MenuRepository(Webservice webservice, AppExecutors appExecutors, LocationDao locationDao, Provider<UpdateMenuTask> menuTaskProvider) {
        mWebservice = webservice;
        mAppExecutors = appExecutors;
        mLocationDao = locationDao;
        mMenuTaskProvider = menuTaskProvider;
    }

    public LiveData<Resource<FullDayMenu>> getMenus(DateTime dateTime, List<Location> locations) {
        MutableLiveData<Resource<FullDayMenu>> data = new MutableLiveData<>();
        if (locations == null) return data;
        UpdateMenuTask task = mMenuTaskProvider.get().forLocations(locations).forDate(dateTime).setLiveData(data);
        mAppExecutors.networkIO().execute(task);
        return data;
    }

    public LiveData<List<Location>> getLocations() {
        mAppExecutors.networkIO().execute(() -> {
            try {
                Response<LocationsResponse> response = mWebservice.getLocations().execute();
                if (response.isSuccessful()) {
                    Log.d(TAG, "getLocations: " + response.body().getLocation());
                    mLocationDao.insertAll(response.body().getLocation());
                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return mLocationDao.getAll();
    }

    // the first time this is called when the app is first installed (has no data) it will emit an empty list
    // this empty list gets sent
    public LiveData<List<Location>> getVisibleLocations() {
        updateLocationsFromNetwork();
        return mLocationDao.getVisible();
    }

    public void updateLocations(Location... locations) {
        mAppExecutors.diskIO().execute(() -> mLocationDao.updateLocations(locations));
    }

    public void updateLocations(List<Location> locations) {
        mAppExecutors.diskIO().execute(() -> mLocationDao.updateLocations(locations));
    }

    private void updateLocationsFromNetwork() {
        mAppExecutors.networkIO().execute(() -> {
            try {
                Response<LocationsResponse> response = mWebservice.getLocations().execute();
                if (response.isSuccessful()) {
                    Log.d(TAG, "getLocations: " + response.body().getLocation());
                    mLocationDao.insertAll(response.body().getLocation());
                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
