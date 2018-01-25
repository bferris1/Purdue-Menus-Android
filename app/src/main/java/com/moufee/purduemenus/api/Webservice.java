package com.moufee.purduemenus.api;

import com.moufee.purduemenus.menus.DiningCourtMenu;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Defines the available HTTP requests for the Menus API
 */

public interface Webservice {

    @Headers({
            "Accept: text/json"
    })
    @GET("/menus/v2/locations/{location}/{date}")
    Call<DiningCourtMenu> getMenu(@Path("location") String diningCourtName, @Path("date") String date);
}
