package com.moufee.purduemenus.api;

import android.content.Context;
import android.util.Log;

import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import okhttp3.Cookie;

public class FileCookiePersistor implements CookiePersistor {

    public static final String TAG = "FilePersistor";
    private static final String fileName = "api.cookies";
    private Context mContext;

    @Inject
    public FileCookiePersistor(Context context) {
        mContext = context;
    }


    @Override
    public List<Cookie> loadAll() {
        File filesDir = mContext.getCacheDir();
        File sourceFile = new File(filesDir, fileName);
        Set<Cookie> set = new HashSet<>();
        ArrayList<Cookie> result = new ArrayList<>();
        ArrayList<SerializableCookie> tempResult;
        if (!sourceFile.exists())
            return new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            tempResult = (ArrayList<SerializableCookie>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            return new ArrayList<>();
        }
        for (SerializableCookie tempCookie :
                tempResult) {
            result.add(tempCookie.getCookie());
        }
        Log.d("ASDF", "loadAll: " + result);
        return result;
    }

    @Override
    public void saveAll(Collection<Cookie> cookies) {

        List<SerializableCookie> toSerialize = new ArrayList<>();
        Set<Cookie> set = new HashSet<>();
        for (Cookie cookie :
                cookies) {
            toSerialize.add(new SerializableCookie(cookie));
        }
        File filesDir = mContext.getCacheDir();
        File outputFile = new File(filesDir, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(toSerialize);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        Log.d(TAG, "removeAll: ");
        List<Cookie> current = loadAll();
        Iterator<Cookie> iterator = current.iterator();
        while (iterator.hasNext()) {
            Cookie next = iterator.next();
            if (cookies.contains(next))
                iterator.remove();

        }
        File filesDir = mContext.getCacheDir();
        File outputFile = new File(filesDir, fileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    @Override
    public void clear() {

    }
}
