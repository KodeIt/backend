package com.kodeit.backend.exception.jwt;

public class JWTTokenBlacklistedException extends Exception{

    public JWTTokenBlacklistedException() {
        super("This token has been blacklisted");
    }

}