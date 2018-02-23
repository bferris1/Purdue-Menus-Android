package com.moufee.purduemenus.api;

import android.arch.lifecycle.MutableLiveData;

import com.moufee.purduemenus.menus.Favorites;

public class GetFavoritesTask implements Runnable {

    private MutableLiveData<Favorites> mFavorites;
    private Webservice mWebservice;

    @Override
    public void run() {

    }
}
