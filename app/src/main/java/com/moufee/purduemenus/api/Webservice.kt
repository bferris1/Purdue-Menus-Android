package com.moufee.purduemenus.api

import com.moufee.purduemenus.api.models.ApiFavoritesResponse
import com.moufee.purduemenus.api.models.LocationsResponse
import com.moufee.purduemenus.api.models.RemoteDiningCourtMenu
import com.moufee.purduemenus.api.models.RemoteFavorite
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
    suspend fun getMenu(@Path("location") diningCourtName: String, @Path("date") date: String): RemoteDiningCourtMenu

    @Headers("Accept: text/json")
    @GET("/menus/v2/favorites")
    suspend fun getFavorites(@Query("ticket") ticket: String?): ApiFavoritesResponse

    @POST("/menus/v2/favorites")
    suspend fun addFavorite(@Body favorite: RemoteFavorite): Response<ResponseBody>

    @DELETE("/menus/v2/favorites/{favoriteID}")
    suspend fun deleteFavorite(@Path("favoriteID") favoriteID: String): Response<ResponseBody>
}
