package com.moufee.purduemenus.api;

import com.moufee.purduemenus.menus.DiningCourtMenu;
import com.moufee.purduemenus.menus.Favorite;
import com.moufee.purduemenus.menus.Favorites;
import com.moufee.purduemenus.menus.LocationsResponse;

import androidx.annotation.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Defines the available HTTP requests for the Menus API
 */

public interface Webservice {

    @Headers({
            "Accept: text/json"
    })
    @GET("/menus/v2/locations/{location}/{date}")
    Call<DiningCourtMenu> getMenu(@Path("location") String diningCourtName, @Path("date") String date);

    @Headers({
            "Accept: text/json"
    })
    @GET("/menus/v2/locations")
    Call<LocationsResponse> getLocations();

    @Headers({
            "Accept: text/json"
    })
    @GET("/menus/v2/favorites")
    Call<Favorites> getFavorites(@Query("ticket") @Nullable String ticket);

    @POST("/menus/v2/favorites")
    Call<ResponseBody> addFavorite(@Body Favorite favorite);

    @DELETE("/menus/v2/favorites/{favoriteID}")
    Call<ResponseBody> deleteFavorite(@Path("favoriteID") String favoriteID);
}
