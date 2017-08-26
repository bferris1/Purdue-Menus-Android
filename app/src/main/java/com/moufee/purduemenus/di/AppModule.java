package com.moufee.purduemenus.di;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moufee.purduemenus.api.LocalTimeTypeConverter;
import com.moufee.purduemenus.util.AppExecutors;
import com.moufee.purduemenus.api.Webservice;

import org.joda.time.LocalTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ben on 28/07/2017.
 * The Dagger App Module, Provides dependencies for injection
 * Update: not using dagger for initial release
 * in progress: use dagger
 */

@Module
class AppModule {
    @Singleton @Provides
     Webservice provideWebService(AppExecutors executors, Gson gson){
        return new Retrofit.Builder()
                .baseUrl("https://api.hfs.purdue.edu")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executors.diskIO())
                .build()
                .create(Webservice.class);
    }
    @Singleton @Provides
     Gson provideGson(){
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeConverter())
                .create();
    }
    @Singleton @Provides
     OkHttpClient provideHttpClient(){
        //todo: restore cookies or not?
        return new OkHttpClient();
    }
}
