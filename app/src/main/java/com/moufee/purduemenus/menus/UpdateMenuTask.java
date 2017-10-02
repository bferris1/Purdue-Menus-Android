package com.moufee.purduemenus.menus;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moufee.purduemenus.MenusApp;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ben on 03/08/2017.
 * A task to update LiveData and cache
 */

public class UpdateMenuTask implements Runnable {

    private MutableLiveData<Resource<FullDayMenu>> mFullMenu;
    private Context mContext;
    private DateTime mMenuDate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String TAG = "UpdateMenuTask";
    private boolean mFetchedFromFile = false;
    @Inject Webservice mWebservice;
    @Inject Gson mGson;
    ConnectivityManager mConnectivityManager;

    public UpdateMenuTask(MutableLiveData<Resource<FullDayMenu>> menu, Context context, DateTime date) {
        mFullMenu = menu;
        mContext = context;
        mMenuDate = date;
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        MenusApp app = (MenusApp) context.getApplicationContext();
        app.getAppComponent().inject(this);
    }

    @Override
    public void run() {
//        mFullMenu.postValue(Resource.<FullDayMenu>loading(null));
        ArrayList<DiningCourtMenu> fileMenus = getMenusFromFile(DATE_TIME_FORMATTER.print(mMenuDate));
        if (fileMenus!=null){
            mFetchedFromFile = true;
            mFullMenu.postValue(Resource.success(new FullDayMenu(fileMenus, mMenuDate, hasLateLunch(fileMenus))));
            Log.d(TAG, "getFullMenu: Read from file!");
        }else{
            mFullMenu.postValue(Resource.<FullDayMenu>loading(null));
        }
        if (fileMenus == null || shouldFetch()){
            fetchFromNetwork();
        }
    }

    private boolean shouldFetch(){
        //todo: also check if the menus from the file are too short, have a lot of null values, etc
        return true;
//        DateTime now = new DateTime();
//        Log.d(TAG, "shouldFetch: days: "+Days.daysBetween(now, mMenuDate).getDays());
//        return Math.abs(Days.daysBetween(now, mMenuDate).getDays()) > 5;
    }

    private boolean hasLateLunch(List<DiningCourtMenu> menus){
        for (DiningCourtMenu menu :
                menus) {
            if (menu.servesLateLunch())
                return true;
        }
        return false;
    }

    private ArrayList<DiningCourtMenu> getMenusFromFile(String formattedDate){
        File filesDir = mContext.getCacheDir();
        File sourceFile = new File(filesDir, formattedDate + ".json");
        ArrayList<DiningCourtMenu> result;
        if (!sourceFile.exists())
            return null;
        FileReader sourceReader = null;
        try {
            sourceReader = new FileReader(sourceFile);
            Type type = new TypeToken<ArrayList<DiningCourtMenu>>(){}.getType();
            result =  mGson.fromJson(sourceReader, type);
        } catch (Exception e) {
                try {
                    if (sourceReader != null)
                    sourceReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            return null;
        }

        try {
            sourceReader.close();
        } catch (IOException e) {
            Log.e(TAG, "getMenusFromFile: ", e);
        }

        return result;
    }

    private void saveMenuToFile(String filename, List<DiningCourtMenu> menus) throws IOException {
        File filesDir = mContext.getCacheDir();
        File outputFile = new File(filesDir, filename);
        FileWriter writer = new FileWriter(outputFile);
        String json = mGson.toJson(menus);
        writer.write(json);
        writer.close();
    }

    private void fetchFromNetwork() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()){
            if (mFetchedFromFile)
                return;
            else
                mFullMenu.postValue(Resource.<FullDayMenu>error("Not connected to network", null));
            return;
        }
        final ArrayList<String> diningCourts = new ArrayList<>(6);
        diningCourts.add("Earhart");
        diningCourts.add("Ford");
        diningCourts.add("Wiley");
        diningCourts.add("Windsor");
        diningCourts.add("Hillenbrand");
        diningCourts.add("The Gathering Place");
        //this is similar to the initial implementation from the architecture components guide
        final List<DiningCourtMenu> tempMenusList = new ArrayList<>();
        final String dateString = DATE_TIME_FORMATTER.print(mMenuDate);
        for (final String diningCourt : diningCourts) {
            Call<DiningCourtMenu> menuCall = mWebservice.getMenu(diningCourt, dateString);
            //// TODO: 20/08/2017 restructure so callbacks don't run on ui thread
//            Response<DiningCourtMenu> response = menuCall.execute();
//            if (response.isSuccessful())
//                tempMenusList.add(response.body());
            menuCall.enqueue(new Callback<DiningCourtMenu>() {
                @Override
                public void onResponse(@NonNull Call<DiningCourtMenu> call, @NonNull Response<DiningCourtMenu> response) {
//                    Log.d(TAG, "onResponse: Success");
                    if (response.isSuccessful())
                        tempMenusList.add(response.body());

                    if (tempMenusList.size() == diningCourts.size()) {
                        Collections.sort(tempMenusList,new Comparator<DiningCourtMenu>() {
                            @Override
                            public int compare(DiningCourtMenu o1, DiningCourtMenu o2) {
                                if (o1 == null || o2 == null)
                                    return 0;
                                if (diningCourts.indexOf(o1.getLocation()) < diningCourts.indexOf(o2.getLocation()))
                                    return -1;
                                if (diningCourts.indexOf(o1.getLocation()) < diningCourts.indexOf(o2.getLocation()))
                                    return 1;
                                return 0;
                            }
                        });
                        mFullMenu.postValue(Resource.success(new FullDayMenu(tempMenusList, mMenuDate, hasLateLunch(tempMenusList))));
                        //save to json
                        try {
                            saveMenuToFile(dateString+".json",tempMenusList);

                        } catch (IOException e) {
                            Log.e(TAG, "onResponse: error saving to file ",e );
                            mFullMenu.postValue(Resource.<FullDayMenu>error(e.getMessage() != null ? e.getMessage() : "an error occurred while saving to file", null));
                        }
                    }
                }

                @Override
                public void onFailure(Call<DiningCourtMenu> call, Throwable t) {
                    //todo: handle failure
                    Log.e(TAG, "onFailure: Network error", t);
                    if (mFullMenu.getValue() != null)
                        mFullMenu.postValue(Resource.error("Network Error", mFullMenu.getValue().data == null ? null : mFullMenu.getValue().data));
                    else
                        mFullMenu.postValue(Resource.<FullDayMenu>error(t.getMessage(), null));
                }
            });


        }
//        if (tempMenusList.size() == diningCourts.size()) {
//            Collections.sort(tempMenusList, new Comparator<DiningCourtMenu>() {
//                @Override
//                public int compare(DiningCourtMenu o1, DiningCourtMenu o2) {
//                    if (o1 == null || o2 == null)
//                        return 0;
//                    if (diningCourts.indexOf(o1.getLocation()) < diningCourts.indexOf(o2.getLocation()))
//                        return -1;
//                    if (diningCourts.indexOf(o1.getLocation()) < diningCourts.indexOf(o2.getLocation()))
//                        return 1;
//                    return 0;
//                }
//            });
//            mFullMenu.postValue(Resource.success(new FullDayMenu(tempMenusList, mMenuDate, hasLateLunch(tempMenusList))));
//            //save to json
//            saveMenuToFile(dateString + ".json", tempMenusList);
//        }
    }
}
