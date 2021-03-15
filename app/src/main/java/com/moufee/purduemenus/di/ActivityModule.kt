package com.moufee.purduemenus.di

import com.moufee.purduemenus.ui.login.LoginActivity
import com.moufee.purduemenus.ui.menu.MenuActivity
import com.moufee.purduemenus.ui.settings.CustomOrderActivity
import com.moufee.purduemenus.ui.settings.SettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * The Dagger Module for MenuActivity
 * Allows MenuActivity and Fragments it hosts to use dependency injection
 */
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMenuActivity(): MenuActivity?

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeSettingsActivity(): SettingsActivity?

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity?

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeCustomOrderActivity(): CustomOrderActivity?
}