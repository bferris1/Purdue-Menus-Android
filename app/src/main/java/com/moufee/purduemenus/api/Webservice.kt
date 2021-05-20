package com.moufee.purduemenus.api

import com.moufee.purduemenus.api.models.ApiDiningCourtMenu
import com.moufee.purduemenus.api.models.ApiFavorite
import com.moufee.purduemenus.api.models.ApiFavoritesResponse
import com.moufee.purduemenus.repository.data.menus.LocationsResponse
import okhttp3.ResponseBody
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
    suspend fun getFavorites(@Query("ticket") ticket: String?): ApiFavoritesResponse

    @POST("/menus/v2/favorites")
    suspend fun addFavorite(@Body favorite: ApiFavorite): Response<ResponseBody>

    @DELETE("/menus/v2/favorites/{favoriteID}")
    suspend fun deleteFavorite(@Path("favoriteID") favoriteID: String): Response<ResponseBody>
}
