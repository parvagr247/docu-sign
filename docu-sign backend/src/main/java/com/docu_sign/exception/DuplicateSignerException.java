package com.docu_sign.exception;

public class DuplicateSignerException extends RuntimeException{

    public DuplicateSignerException( String message ) {
            super(message);
    }
}

