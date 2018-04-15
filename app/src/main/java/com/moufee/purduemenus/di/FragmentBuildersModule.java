package com.moufee.purduemenus.di;

import com.moufee.purduemenus.ui.menu.MenuItemListFragment;
import com.moufee.purduemenus.ui.settings.CustomOrderFragment;
import com.moufee.purduemenus.ui.settings.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Defines the Fragments that may be injected
 */

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract MenuItemListFragment contributeMenuItemListFragment();

    @ContributesAndroidInjector
    abstract CustomOrderFragment contributeCustomOrderFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();
}
