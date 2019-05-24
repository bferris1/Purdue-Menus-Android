package com.moufee.purduemenus.menus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.moufee.purduemenus.api.MenuDownloader;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.util.Resource;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private Webservice mWebservice;
    private ConnectivityManager mConnectivityManager;
    private List<Location> mLocationList;


    public UpdateMenuTask(MutableLiveData<Resource<FullDayMenu>> liveData, List<Location> locations, Context context, Webservice webservice) {
        this.mFullMenu = liveData;
        mLocationList = locations;
        mContext = context;
        mWebservice = webservice;
        mMenuDate = new DateTime();
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //todo: what is this for? constructor too long?

    public UpdateMenuTask withDate(DateTime date) {
        this.mMenuDate = date;
        return this;
    }

    @Override
    public void run() {
//        mFullMenu.postValue(Resource.<FullDayMenu>loading(null));
        FullDayMenu fileMenus = getMenusFromFile(DATE_TIME_FORMATTER.print(mMenuDate));
        if (fileMenus != null) {
            mFetchedFromFile = true;
            mFullMenu.postValue(Resource.success(fileMenus));
            Log.d(TAG, "getFullMenu: Read from file!");
        } else {
            mFullMenu.postValue(Resource.loading(null));
        }
        if (fileMenus == null || shouldFetch()) {
            fetchFromNetwork();
        }
    }

    private boolean shouldFetch() {
        //todo: check if the menus from the file are too short, have a lot of null values, etc and decide when to fetch?
        return true;
//        DateTime now = new DateTime();
//        Log.d(TAG, "shouldFetch: days: "+Days.daysBetween(now, mMenuDate).getDays());
//        return Math.abs(Days.daysBetween(now, mMenuDate).getDays()) > 5;
    }

    private FullDayMenu getMenusFromFile(String formattedDate) {
        File filesDir = mContext.getCacheDir();
        File sourceFile = new File(filesDir, formattedDate + ".menus");
        FullDayMenu result;
        if (!sourceFile.exists())
            return null;
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            result = (FullDayMenu) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "getMenusFromFile: error", e);
            return null;
        }
        return result;
    }

    private void saveMenuToFile(String filename, FullDayMenu menu) throws IOException {
        File filesDir = mContext.getCacheDir();
        File outputFile = new File(filesDir, filename);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(menu);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    // todo: move this logic to ViewModel?

    private void sortMenus(List<DiningCourtMenu> menus) {
        Log.d(TAG, "sortMenus: " + menus.toString());
        Map<String, Location> locationMap = new HashMap<>();
        for (int i = 0; i < mLocationList.size(); i++) {
            Location location = mLocationList.get(i);
            locationMap.put(location.getName(), location);
        }

        Iterator<DiningCourtMenu> iterator = menus.iterator();
        while (iterator.hasNext()) {
            DiningCourtMenu menu = iterator.next();
            if (locationMap.get(menu.getLocation()).isHidden()) {
                iterator.remove();
            }
        }
        Collections.sort(menus, new DiningCourtComparator(mLocationList));
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
        List<String> diningCourtNames = new ArrayList<>();
        for (Location location :
                mLocationList) {
            diningCourtNames.add(location.getName());
        }
        //this is similar to the initial implementation from the architecture components guide
        final String dateString = DATE_TIME_FORMATTER.print(mMenuDate);
        new MenuDownloader(mWebservice, mContext) {
            @Override
            public void onComplete(@NotNull FullDayMenu fullDayMenu) {
                mFullMenu.postValue(Resource.success(fullDayMenu));
                //save to json
                try {
                    saveMenuToFile(dateString + ".menus", fullDayMenu);

                } catch (IOException e) {
                    Log.e(TAG, "onResponse: error saving to file ", e);
                    mFullMenu.postValue(Resource.error(e.getMessage() != null ? e.getMessage() : "an error occurred while saving to file", null));
                }
            }

            @Override
            public void onFailure(@NotNull String reason) {
                if (mFullMenu.getValue() != null)
                    mFullMenu.postValue(Resource.error("Network Error", mFullMenu.getValue().data));
                else
                    mFullMenu.postValue(Resource.error(reason, null));
            }
        }.getMenus(mMenuDate, mLocationList);
    }
}
