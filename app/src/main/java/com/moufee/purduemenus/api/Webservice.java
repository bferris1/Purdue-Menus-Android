package com.moufee.purduemenus.api;

import com.moufee.purduemenus.menus.DiningCourtMenu;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Ben on 17/07/2017.
 */

public interface Webservice {

    @Headers({
            "Accept: text/json"
    })
    @GET("/menus/v2/locations/{location}/{date}")
    Call<DiningCourtMenu> getMenu(@Path("location") String diningCourtName, @Path("date") String date);
}
