package com.bmk.bmkbookings.exception;

public class ServiceNotAvailableException extends Exception{
    public ServiceNotAvailableException(Long serviceId){
        super("Mercchant does not offer the service with id:"+serviceId);
    }
}
