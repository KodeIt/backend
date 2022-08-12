package com.kodeit.backend.exception.user;

public class UserNotFoundException extends Exception{

    public UserNotFoundException () {
        super("The provided user was not found");
    }

}

