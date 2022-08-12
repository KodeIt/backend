package com.kodeit.backend.exception.code;

public class CodeNotFoundException extends CodeException{
    public CodeNotFoundException() {
        super("The requested code was not found!");
    }
}
