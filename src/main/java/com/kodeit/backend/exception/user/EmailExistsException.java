package com.kodeit.backend.exception.user;

public class EmailExistsException extends Exception {

    public EmailExistsException() {
        super("The email already exists!");
    }

}
