package com.bmk.bmkbookings.exception;

public class UnauthorizedUserException extends Exception{
    public UnauthorizedUserException(){
        super("User is unauthorized");
    }
}
