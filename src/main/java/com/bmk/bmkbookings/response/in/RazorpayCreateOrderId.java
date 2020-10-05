package com.bmk.bmkbookings.response.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RazorpayCreateOrderId {
    String id;
    String entity;
    String amount;
    String amount_paid;
    String amount_due;
    String currency;
    String receipt;
    String status;
    String offer_id;
    int attempts;
    String[] notes;
    long created_at;
}
