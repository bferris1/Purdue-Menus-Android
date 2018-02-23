package com.moufee.purduemenus.di;

import com.moufee.purduemenus.ui.login.LoginActivity;
import com.moufee.purduemenus.ui.menu.MenuActivity;
import com.moufee.purduemenus.ui.settings.SettingsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * The Dagger Module for MenuActivity
 * Allows MenuActivity and Fragments it hosts to use dependency injection
 */
@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MenuActivity contributeMenuActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();
}
