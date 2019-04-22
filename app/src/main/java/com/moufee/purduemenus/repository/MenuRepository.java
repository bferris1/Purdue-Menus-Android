package com.moufee.purduemenus.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.db.FavoriteDao;
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
    private Gson mGson;
    private Context mApplicationContext;
    private AppExecutors mAppExecutors;
    private FavoriteDao mFavoriteDao;
    private LocationDao mLocationDao;

    @Inject
    public MenuRepository(Webservice webservice, Gson gson, Context applicationContext, AppExecutors appExecutors, FavoriteDao favoriteDao, LocationDao locationDao) {
        mWebservice = webservice;
        mGson = gson;
        mApplicationContext = applicationContext;
        mAppExecutors = appExecutors;
        mFavoriteDao = favoriteDao;
        mLocationDao = locationDao;
    }

    public LiveData<Resource<FullDayMenu>> getMenus(DateTime dateTime, List<Location> locations) {
        MutableLiveData<Resource<FullDayMenu>> data = new MutableLiveData<>();
        if (locations == null) return data;
        UpdateMenuTask task = new UpdateMenuTask(data, locations, mApplicationContext, mWebservice).withDate(dateTime);
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


}
