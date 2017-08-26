package com.moufee.purduemenus.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.moufee.purduemenus.menus.UpdateMenuTask;
import com.moufee.purduemenus.util.Resource;
import com.moufee.purduemenus.menus.FullDayMenu;

import org.joda.time.DateTime;


/**
 * Created by Ben on 22/07/2017.
 * Where all the menus live
 * Abstracts the data sources from the rest of the app
 * Creates threads to retrieve and process menu data
 */

//todo (in progress): dagger dependency injection?

public class MenuRepository {

    private static MenuRepository sMenuRepository;
    private static final String TAG = "MenuRepository";

    public static MenuRepository get(){
        if (sMenuRepository == null)
            sMenuRepository = new MenuRepository();
        return sMenuRepository;
    }

    public LiveData<Resource<FullDayMenu>> getMenus(Context context){
        return getMenus(context, new DateTime());
    }


    public LiveData<Resource<FullDayMenu>> getMenus(Context context, DateTime dateTime){
        MutableLiveData<Resource<FullDayMenu>> data = new MutableLiveData<>();
        UpdateMenuTask task = new UpdateMenuTask(data, context, dateTime);
        Thread t = new Thread(task);
        t.start();
        return data;
    }


}
