package com.moufee.purduemenus.di;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moufee.purduemenus.api.LocalTimeTypeConverter;
import com.moufee.purduemenus.util.AppExecutors;
import com.moufee.purduemenus.api.Webservice;

import org.joda.time.LocalTime;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ben on 28/07/2017.
 * The Dagger App Module, Provides dependencies for injection
 * Update: not using dagger for initial release
 * todo: use dagger
 */


public class AppModule {

    static Webservice sWebservice;
    static Gson sGson;
    static OkHttpClient sHttpClient;
    static AppExecutors sAppExecutors;

    public static Webservice getWebservice(){
        if (sWebservice == null)
            sWebservice = provideWebService();
        return sWebservice;
    }

    public static Gson getGson(){
        if (sGson == null)
            sGson = provideGson();
        return sGson;
    }

    public static OkHttpClient getHttpClient(){
        if (sHttpClient == null)
            sHttpClient = provideHttpClient();
        return sHttpClient;
    }

    public static AppExecutors getAppExecutors(){
        if (sAppExecutors == null)
            sAppExecutors = new AppExecutors();
        return sAppExecutors;
    }

    private static Webservice provideWebService(){
        Gson gson = getGson();

        return new Retrofit.Builder()
                .baseUrl("https://api.hfs.purdue.edu")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Webservice.class);
    }

    private static Gson provideGson(){
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeConverter())
                .create();
    }
    private static OkHttpClient provideHttpClient(){
        //todo: restore cookies or not?
        return new OkHttpClient();
    }
}
