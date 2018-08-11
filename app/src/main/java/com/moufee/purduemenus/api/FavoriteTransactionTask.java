package com.moufee.purduemenus.api;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moufee.purduemenus.util.AuthHelper;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

public abstract class FavoriteTransactionTask<T> implements Runnable {


    private OkHttpClient mHttpClient;
    private SharedPreferences mSharedPreferences;
    private static final String TAG = "FavoriteTransaction";

    public FavoriteTransactionTask(OkHttpClient httpClient, SharedPreferences sharedPreferences) {
        mHttpClient = httpClient;
        mSharedPreferences = sharedPreferences;
    }

    public void run() {

        if (!isLoggedIn()) return;
        try {
            Call<T> initialCall = getCall(null);
            Response<T> response = initialCall.execute();
            Log.d(TAG, "run: " + response);
            if (response.isSuccessful()) {
                onSuccess(response);
            } else if (response.code() == 401) {
                Call<T> secondCall = getCall(getTicket(initialCall.request().url().toString()));
                response = secondCall.execute();
                Log.d(TAG, "run: " + response);
                if (response.isSuccessful() || response.code() == 405) {
                    if (!secondCall.request().method().equals("GET")) {
                        Log.d(TAG, "run: method is " + secondCall.request().method());
                        // have to make the request again
                        response = initialCall.clone().execute();
                        if (response.isSuccessful())
                            onSuccess(response);
                        else
                            Log.d(TAG, "run: " + response);
                    } else
                        onSuccess(response);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
        }

    }


    public abstract Call<T> getCall(@Nullable String ticket);

    public abstract void onSuccess(Response<T> response);

    private boolean isLoggedIn() {

        if (!mSharedPreferences.getBoolean("logged_in", false))
            return false;
        if (!mSharedPreferences.contains("username") || !mSharedPreferences.contains("password")) {
            mSharedPreferences.edit().putBoolean("logged_in", false).apply();
            return false;
        }

        return true;
    }


    private String getTicket(String service) throws IOException {
        String username = mSharedPreferences.getString("username", "");
        String password = mSharedPreferences.getString("password", "");
        Request firstRequest = AuthHelper.getTGTRequest(username, password);

        okhttp3.Response response = mHttpClient.newCall(firstRequest).execute();
        if (!response.isSuccessful()) {
            mSharedPreferences.edit()
                    .putBoolean("logged_in", false)
                    .apply();
            return null;
        }

        String location = response.headers().get("Location");
        Log.d(TAG, "doInBackground: location: " + location);

        if (location == null)
            return null;


        Request ticketRequest = AuthHelper.getTicketRequest(location, service);

        okhttp3.Response ticketResponse = mHttpClient.newCall(ticketRequest).execute();
        if (!ticketResponse.isSuccessful()) {
            Log.d(TAG, "doInBackground: Ticket response not successful");
            return null;
        }
        String ticket = ticketResponse.body().string().trim();
        Log.d(TAG, "doInBackground: ticket: " + ticket);

        return ticket;

    }
}
