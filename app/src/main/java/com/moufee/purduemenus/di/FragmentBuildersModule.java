package com.moufee.purduemenus.di;

import com.moufee.purduemenus.ui.menu.MenuItemListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Defines the Fragments that may be injected
 */

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract MenuItemListFragment contributeMenuItemListFragment();
}
