package com.moufee.purduemenus.di;

import android.app.Application;

import com.moufee.purduemenus.MenusApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Defines where dependency injection may be performed
 * In progress: convert to Android dependency injection
 */
@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class, MenuActivityModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(MenusApp app);

}
