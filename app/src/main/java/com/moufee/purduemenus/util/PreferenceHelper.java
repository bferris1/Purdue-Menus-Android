package com.moufee.purduemenus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ben on 9/21/17.
 */

public class PreferenceHelper {
    private static PreferenceHelper preferenceHelper;
    private SharedPreferences sharedPreferences;

    private PreferenceHelper(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceHelper get(Context applicationContext){
        if (preferenceHelper == null)
            preferenceHelper = new PreferenceHelper(applicationContext);
        return preferenceHelper;
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }
}
