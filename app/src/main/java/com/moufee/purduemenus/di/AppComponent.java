package com.moufee.purduemenus.di;

import com.moufee.purduemenus.menus.UpdateMenuTask;
import com.moufee.purduemenus.ui.menu.MenuActivity;
import com.moufee.purduemenus.ui.settings.SettingsActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Defines where dependency injection may be performed
 * In progress: convert to Android dependency injection
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {


    void inject(UpdateMenuTask task);

    void inject(MenuActivity menuActivity);

    void inject(SettingsActivity settingsActivity);
}
