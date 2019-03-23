package com.moufee.purduemenus.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.menus.FullDayMenu;
import com.moufee.purduemenus.menus.LocationsResponse;
import com.moufee.purduemenus.menus.UpdateMenuTask;
import com.moufee.purduemenus.util.AppExecutors;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    @Inject
    public MenuRepository(Webservice webservice, Gson gson, Context applicationContext, AppExecutors appExecutors, FavoriteDao favoriteDao) {
        mWebservice = webservice;
        mGson = gson;
        mApplicationContext = applicationContext;
        mAppExecutors = appExecutors;
        mFavoriteDao = favoriteDao;
    }

    public LiveData<Resource<FullDayMenu>> getMenus() {
        return getMenus(new DateTime());
    }


    public LiveData<Resource<FullDayMenu>> getMenus(DateTime dateTime) {
        MutableLiveData<Resource<FullDayMenu>> data = new MutableLiveData<>();
        UpdateMenuTask task = new UpdateMenuTask(data, mApplicationContext, mWebservice, mGson, mFavoriteDao).withDate(dateTime);
        mAppExecutors.diskIO().execute(task);
        return data;
    }

    public LiveData<Resource<LocationsResponse>> getLocations() {
        MutableLiveData<Resource<LocationsResponse>> data = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {
            try {
                Response<LocationsResponse> response = mWebservice.getLocations().execute();
                if (response.isSuccessful()) {
                    Log.d(TAG, "getLocations: " + response.body().getLocation());
                    data.postValue(Resource.success(response.body()));
                } else {
                    data.postValue(Resource.error("Error retrieving locations.", null));
                }
            } catch (IOException e) {
                e.printStackTrace();
                data.postValue(Resource.error(e.getMessage(), null));
            }
        });
        return data;
    }


}
