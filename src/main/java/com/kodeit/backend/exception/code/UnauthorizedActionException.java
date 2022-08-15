package com.kodeit.backend.exception.code;

public class UnauthorizedActionException extends CodeException{
    public UnauthorizedActionException() {
        super("The action is not permitted");
    }
}
