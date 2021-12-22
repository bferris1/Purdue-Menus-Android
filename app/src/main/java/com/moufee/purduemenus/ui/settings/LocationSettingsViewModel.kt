package com.moufee.purduemenus.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.repository.data.menus.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class LocationSettingsViewModel
@Inject
constructor(menuRepository: MenuRepository) : ViewModel() {
    val locations: LiveData<List<Location>> = menuRepository.locations

    var orderedLocations: MutableList<Location> = ArrayList()


}
