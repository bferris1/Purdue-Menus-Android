package com.moufee.purduemenus.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.moufee.purduemenus.menus.DailyMenuViewModel;

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
    abstract ViewModelProvider.Factory bindViewModelFactory(MenusAppViewModelFactory factory);
}
