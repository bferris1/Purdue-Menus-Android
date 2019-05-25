package com.moufee.purduemenus.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moufee.purduemenus.api.MenuDownloader;
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
    private Context mApplicationContext;
    private AppExecutors mAppExecutors;
    private LocationDao mLocationDao;
    private MenuDownloader mMenuDownloader;

    @Inject
    public MenuRepository(Webservice webservice, Context applicationContext, AppExecutors appExecutors, LocationDao locationDao, MenuDownloader menuDownloader) {
        mWebservice = webservice;
        mApplicationContext = applicationContext;
        mAppExecutors = appExecutors;
        mLocationDao = locationDao;
        mMenuDownloader = menuDownloader;
    }

    public LiveData<Resource<FullDayMenu>> getMenus(DateTime dateTime, List<Location> locations) {
        MutableLiveData<Resource<FullDayMenu>> data = new MutableLiveData<>();
        if (locations == null) return data;
        UpdateMenuTask task = new UpdateMenuTask(data, locations, mApplicationContext, mMenuDownloader).withDate(dateTime);
        mAppExecutors.diskIO().execute(task);
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

    public LiveData<List<Location>> getVisibleLocations() {
        return mLocationDao.getVisible();
    }

    public void updateLocations(Location... locations) {
        mAppExecutors.diskIO().execute(() -> mLocationDao.updateLocations(locations));
    }

    public void updateLocations(List<Location> locations) {
        mAppExecutors.diskIO().execute(() -> mLocationDao.updateLocations(locations));
    }


}
