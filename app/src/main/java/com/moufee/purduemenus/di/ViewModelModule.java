package com.moufee.purduemenus.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.moufee.purduemenus.ui.menu.DailyMenuViewModel;
import com.moufee.purduemenus.ui.settings.LocationSettingsViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Maps ViewModel classes to ViewModel (providers)
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DailyMenuViewModel.class)
    abstract ViewModel bindListViewModel(DailyMenuViewModel dailyMenuViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LocationSettingsViewModel.class)
    abstract ViewModel bindLocationsViewModel(LocationSettingsViewModel locationSettingsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MenusAppViewModelFactory factory);
}
