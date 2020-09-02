package com.moufee.purduemenus.api;

import android.content.SharedPreferences;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;

// todo: unnecessary generic?
public abstract class AuthenticatedAPITask<T> implements Runnable {


    private SharedPreferences mSharedPreferences;
    private static final String TAG = "FavoriteTransaction";

    public AuthenticatedAPITask(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void run() {

        if (!isLoggedIn()) return;
        try {
            Call<T> initialCall = getCall();
            Response<T> response = initialCall.execute();
            Log.d(TAG, "run: " + response);
            if (response.isSuccessful()) {
                onSuccess(response);
            } else {
                //logout or something
                mSharedPreferences.edit().putBoolean("logged_in", false).apply();
                Log.d(TAG, "run: unsuccessful response" + response.message());
            }
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
        }

    }


    public abstract Call<T> getCall();

    public abstract void onSuccess(Response<T> response);

    private boolean isLoggedIn() {
        return mSharedPreferences.getBoolean("logged_in", false);
    }
}
