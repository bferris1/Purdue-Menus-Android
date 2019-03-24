package com.moufee.purduemenus.menus

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Location (usually a dining court) for which menus can be retrieved
 */
@Entity
data class Location(val Name: String, @PrimaryKey val LocationId: String, val FormalName: String, var displayOrder: Int = 0)


