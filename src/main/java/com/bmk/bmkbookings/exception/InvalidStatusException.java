package com.bmk.bmkbookings.exception;

public class InvalidStatusException extends Exception{
    public InvalidStatusException(){
        super("Status oes not exist");
    }
}
