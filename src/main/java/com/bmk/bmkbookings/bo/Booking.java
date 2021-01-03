package com.bmk.bmkbookings.bo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long bookingId;
    Long clientId;
    Long merchantId;
    String serviceIdCsv;
    @NotNull(message = "Date is mandatory")
    Date date;
    String status;
    String razorpayOrderId;
    int payableAmount;
}