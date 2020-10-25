package com.example.cloud.storage.common;

public class AuthResponse extends AbstractMessage{

    private boolean isAuth = false;

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public AuthResponse(boolean isAuth) {
        this.isAuth = isAuth;
    }
}
