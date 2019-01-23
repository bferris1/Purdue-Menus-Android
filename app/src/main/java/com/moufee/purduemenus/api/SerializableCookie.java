package com.moufee.purduemenus.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import okhttp3.Cookie;

public class SerializableCookie implements Serializable {
    private transient Cookie mCookie;

    public SerializableCookie(Cookie cookie) {
        this.mCookie = cookie;
    }

    public Cookie getCookie() {
        return mCookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.mCookie.name());
        out.writeObject(this.mCookie.value());
        out.writeLong(this.mCookie.persistent() ? this.mCookie.expiresAt() : -1L);
        out.writeObject(this.mCookie.domain());
        out.writeObject(this.mCookie.path());
        out.writeBoolean(this.mCookie.secure());
        out.writeBoolean(this.mCookie.httpOnly());
        out.writeBoolean(this.mCookie.hostOnly());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Cookie.Builder builder = new Cookie.Builder();
        builder.name((String) in.readObject());
        builder.value((String) in.readObject());
        long expiresAt = in.readLong();
        if (expiresAt != -1L) {
            builder.expiresAt(expiresAt);
        }

        String domain = (String) in.readObject();
        builder.domain(domain);
        builder.path((String) in.readObject());
        if (in.readBoolean()) {
            builder.secure();
        }

        if (in.readBoolean()) {
            builder.httpOnly();
        }

        if (in.readBoolean()) {
            builder.hostOnlyDomain(domain);
        }

        this.mCookie = builder.build();
    }
}
