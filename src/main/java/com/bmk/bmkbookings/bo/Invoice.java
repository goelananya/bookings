package com.bmk.bmkbookings.bo;

import com.bmk.bmkbookings.response.out.InvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    String invoiceId;
    double totalAmount;
    List<InvoiceItem> invoiceItems;
}
