package com.moufee.purduemenus.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moufee.purduemenus.ui.login.LoginViewModel
import com.moufee.purduemenus.ui.menu.DailyMenuViewModel
import com.moufee.purduemenus.ui.settings.LocationSettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Maps ViewModel classes to ViewModel (providers)
 */
@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DailyMenuViewModel::class)
    abstract fun bindListViewModel(dailyMenuViewModel: DailyMenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocationSettingsViewModel::class)
    abstract fun bindLocationsViewModel(locationSettingsViewModel: LocationSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: MenusAppViewModelFactory): ViewModelProvider.Factory
}