package com.bmk.bmkbookings.response.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceItem {
    String serviceName;
    double amount;
}
