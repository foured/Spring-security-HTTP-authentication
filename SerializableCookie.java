package org.example;

import okhttp3.Cookie;

import java.io.Serializable;

public class SerializableCookie implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String value;
    private long expiresAt;
    private String domain;
    private String path;
    private boolean secure;
    private boolean httpOnly;
    private boolean hostOnly;

    public SerializableCookie(Cookie cookie) {
        this.name = cookie.name();
        this.value = cookie.value();
        this.expiresAt = cookie.expiresAt();
        this.domain = cookie.domain();
        this.path = cookie.path();
        this.secure = cookie.secure();
        this.httpOnly = cookie.httpOnly();
        this.hostOnly = cookie.hostOnly();
    }

    public Cookie toCookie() {
        Cookie.Builder cookieBuilder = new Cookie.Builder()
                .name(name)
                .value(value)
                .expiresAt(expiresAt)
                .domain(domain)
                .path(path);

        if (secure) {
            cookieBuilder.secure();
        }

        if (httpOnly) {
            cookieBuilder.httpOnly();
        }

        if (hostOnly) {
            cookieBuilder.hostOnlyDomain(domain);
        }

        return cookieBuilder.build();
    }
}
