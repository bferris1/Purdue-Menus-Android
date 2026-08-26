package com.moufee.purduemenus.ui.settings

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.moufee.purduemenus.repository.MenuRepository
import com.moufee.purduemenus.repository.data.menus.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationSettingsViewModel
@Inject
constructor(private val menuRepository: MenuRepository) : ViewModel() {
    val locations: LiveData<List<Location>> = menuRepository.locations

    /**
     * Once initialized, this list is the single source of truth for the location info.
     * If we updated it every time the database emits, updates could happen out of order
     * with respect to UI changes, resulting in bad data being saved.
     */
    val orderedLocations = mutableStateListOf<Location>()

    fun initializeOnce(list: List<Location>) {
        if (orderedLocations.isEmpty()) orderedLocations.addAll(list)
    }

    fun moveLocation(fromIndex: Int, toIndex: Int) {
        orderedLocations.add(toIndex, orderedLocations.removeAt(fromIndex))
    }

    fun saveOrder() {
        orderedLocations.forEachIndexed { index, location -> location.displayOrder = index }
        menuRepository.updateLocations(orderedLocations.toList())
    }

    fun toggleVisibility(location: Location) {
        val index = orderedLocations.indexOfFirst { it.LocationId == location.LocationId }
        if (index >= 0) {
            val updated = orderedLocations[index].copy(isHidden = !orderedLocations[index].isHidden)
            orderedLocations[index] = updated
            menuRepository.updateLocations(updated)
        }
    }
}
