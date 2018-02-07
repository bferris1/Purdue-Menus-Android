package com.moufee.purduemenus.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.google.gson.Gson;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.menus.FullDayMenu;
import com.moufee.purduemenus.menus.UpdateMenuTask;
import com.moufee.purduemenus.util.AppExecutors;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;

import javax.inject.Inject;


/**
 * Created by Ben on 22/07/2017.
 * Where all the menus live
 * Abstracts the data sources from the rest of the app
 * Creates threads to retrieve and process menu data
 */

//todo (in progress): dagger dependency injection?

public class MenuRepository {

    private static final String TAG = "MenuRepository";

    private Webservice mWebservice;
    private Gson mGson;
    private Context mApplicationContext;
    private AppExecutors mAppExecutors;

    @Inject
    public MenuRepository(Webservice webservice, Gson gson, Context applicationContext, AppExecutors appExecutors) {
        mWebservice = webservice;
        mGson = gson;
        mApplicationContext = applicationContext;
        mAppExecutors = appExecutors;
    }

    public LiveData<Resource<FullDayMenu>> getMenus() {
        return getMenus(new DateTime());
    }


    public LiveData<Resource<FullDayMenu>> getMenus(DateTime dateTime) {
        MutableLiveData<Resource<FullDayMenu>> data = new MutableLiveData<>();
        UpdateMenuTask task = new UpdateMenuTask(data, mApplicationContext, mWebservice, mGson).withDate(dateTime);
        mAppExecutors.diskIO().execute(task);
        return data;
    }


}
