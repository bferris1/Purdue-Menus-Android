package com.moufee.purduemenus.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

@Singleton
public class ApiCookieJar implements CookieJar {

    @Inject
    public ApiCookieJar() {
    }

    private final Set<Cookie> cookieStore = new LinkedHashSet<>();

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.addAll(cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> matchingCookies = new ArrayList<>();
        Iterator<Cookie> it = cookieStore.iterator();
        while (it.hasNext()) {
            Cookie cookie = it.next();
            if (cookie.expiresAt() < System.currentTimeMillis()) {
                it.remove();
            } else if (cookie.matches(url)) {
                matchingCookies.add(cookie);
            }
        }
        return matchingCookies;
    }
}
