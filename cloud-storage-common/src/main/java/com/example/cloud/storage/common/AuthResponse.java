package com.example.cloud.storage.common;

public class AuthResponse extends AbstractMessage {

    private boolean isAuth;

    public boolean isAuth() {
        return isAuth;
    }

    public AuthResponse(boolean isAuth) {
        this.isAuth = isAuth;
    }
}
