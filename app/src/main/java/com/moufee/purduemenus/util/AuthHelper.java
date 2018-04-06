package com.moufee.purduemenus.util;

import okhttp3.FormBody;
import okhttp3.Request;

public class AuthHelper {

    public static Request getTGTRequest(String username, String password) {
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        String ticketURL = "https://www.purdue.edu/apps/account/cas/v1/tickets";
        return new Request.Builder()
                .url(ticketURL)
                .post(formBody)
                .build();
    }

    public static Request getTicketRequest(String location) {
        String favoritesURL = "https://api.hfs.purdue.edu/menus/v2/favorites";
        FormBody ticketRequestBody = new FormBody.Builder()
                .add("service", favoritesURL)
                .build();

        return new Request.Builder()
                .url(location)
                .post(ticketRequestBody)
                .build();
    }
}
