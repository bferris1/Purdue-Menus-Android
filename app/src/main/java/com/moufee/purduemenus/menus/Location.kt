package com.moufee.purduemenus.menus

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Location (usually a dining court) for which menus can be retrieved
 */
@Keep
@Entity
data class Location(val Name: String, @PrimaryKey val LocationId: String, val FormalName: String, var displayOrder: Int = 0, var isHidden: Boolean = false)


