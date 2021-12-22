package com.moufee.purduemenus.repository.data.menus

import androidx.annotation.Keep
import com.squareup.moshi.Json

/**
 * A container for a list of favorites
 */
@Keep
class Favorites {
    @Json(name = "Favorite")
    val favorites: List<Favorite>? = null
}