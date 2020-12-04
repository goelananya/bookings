package com.bmk.bmkbookings.enums;

import com.bmk.bmkbookings.exception.InvalidStatusException;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum BookingStatusEnum {

    PENDING("pending"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    ACCEPTED("accepted"),
    REQUESTED_CHANGES("requestedChanges"),
    EXPIRED("expired");

    private final String value;

    BookingStatusEnum(final String val) {this.value = val;}

    public static BookingStatusEnum constructObjectFromValue(String key) throws InvalidStatusException {
        for(BookingStatusEnum bookingStatusEnum: BookingStatusEnum.values()) {
            if(bookingStatusEnum.getValue().equalsIgnoreCase(key)) {
                return bookingStatusEnum;
            }
        }
        throw new InvalidStatusException();
    }
}