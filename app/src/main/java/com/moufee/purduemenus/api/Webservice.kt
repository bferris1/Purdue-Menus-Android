package com.moufee.purduemenus.api

import com.moufee.purduemenus.api.models.ApiDiningCourtMenu
import com.moufee.purduemenus.repository.data.menus.Favorite
import com.moufee.purduemenus.repository.data.menus.Favorites
import com.moufee.purduemenus.repository.data.menus.LocationsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * Defines the available HTTP requests for the Menus API
 */

interface Webservice {

    @Headers("Accept: text/json")
    @GET("/menus/v2/locations")
    suspend fun getLocations(): Response<LocationsResponse>

    @Headers("Accept: text/json")
    @GET("/menus/v2/locations/{location}/{date}")
    suspend fun getMenu(@Path("location") diningCourtName: String, @Path("date") date: String): ApiDiningCourtMenu

    @Headers("Accept: text/json")
    @GET("/menus/v2/favorites")
    fun getFavorites(@Query("ticket") ticket: String?): Call<Favorites>

    @POST("/menus/v2/favorites")
    fun addFavorite(@Body favorite: Favorite): Call<ResponseBody>

    @DELETE("/menus/v2/favorites/{favoriteID}")
    fun deleteFavorite(@Path("favoriteID") favoriteID: String): Call<ResponseBody>
}
