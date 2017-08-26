package com.moufee.purduemenus.di;

import com.moufee.purduemenus.menus.UpdateMenuTask;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by Ben on 25/08/2017.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {


    void inject(UpdateMenuTask task);
}
