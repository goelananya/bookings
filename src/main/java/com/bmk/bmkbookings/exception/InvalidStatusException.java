package com.bmk.bmkbookings.exception;

import com.bmk.bmkbookings.enums.BookingStatusEnum;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvalidStatusException extends Exception{
    public InvalidStatusException(){
        super("Status does not exist. Allowed values are:"+ Stream.of(BookingStatusEnum.values()).map(BookingStatusEnum::getValue).collect(Collectors.toList()));
    }
}
