package com.moufee.purduemenus.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.room.Room;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.moufee.purduemenus.BuildConfig;
import com.moufee.purduemenus.api.DateTimeTypeAdapter;
import com.moufee.purduemenus.api.LocalDateAdapter;
import com.moufee.purduemenus.api.LocalTimeTypeAdapter;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.db.AppDatabase;
import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.db.LocationDao;
import com.moufee.purduemenus.util.AppExecutors;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Ben on 28/07/2017.
 * The Dagger App Module, Provides dependencies for injection
 */

@Module(includes = { ViewModelModule.class }) class AppModule {

    @Singleton @Provides Webservice provideWebService(AppExecutors executors, Moshi moshi, OkHttpClient client) {
        return new Retrofit.Builder().baseUrl("https://api.hfs.purdue.edu")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .callbackExecutor(executors.diskIO())
                .build()
                .create(Webservice.class);
    }

    @Singleton @Provides Moshi provideMoshi() {
        return new Moshi.Builder().add(new LocalTimeTypeAdapter())
                .add(new DateTimeTypeAdapter())
                .add(new LocalDateAdapter())
                .add(new KotlinJsonAdapterFactory())
                .build();
    }

    @Singleton @Provides OkHttpClient provideHttpClient(Context context) {
        //todo: maybe don't use shared prefs, possibly use custom implementation
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        builder.cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context)));

        return builder.build();
    }

    @Singleton @Provides SharedPreferences sharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Singleton @Provides Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides FirebaseAnalytics provideAnalytics(Context context) {
        return FirebaseAnalytics.getInstance(context);
    }

    @Provides FirebaseCrashlytics provideCrashlytics() {
        return FirebaseCrashlytics.getInstance();
    }

    @Singleton @Provides AppDatabase provideAppDatabase(Context applicationContext) {
        return Room.databaseBuilder(applicationContext, AppDatabase.class, "menus-database")
                .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides FavoriteDao provideFavoriteDao(AppDatabase appDatabase) {
        return appDatabase.favoriteDao();
    }

    @Provides LocationDao provideLocationDao(AppDatabase appDatabase) {
        return appDatabase.locationDao();
    }

    @Singleton @Provides AppUpdateManager provideUpdateManager(Context context) {
        return AppUpdateManagerFactory.create(context);
    }
}
