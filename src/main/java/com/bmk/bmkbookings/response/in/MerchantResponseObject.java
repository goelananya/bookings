package com.bmk.bmkbookings.response.in;

import com.bmk.bmkbookings.response.out.MerchantResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantResponseObject {
    String statusCode;
    MerchantResponse message;
}
