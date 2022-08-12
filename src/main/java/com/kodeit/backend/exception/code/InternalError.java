package com.kodeit.backend.exception.code;

public class InternalError extends CodeException{

    public InternalError() {
        super("Internal server error while compilation. Please try again!");
    }
}
