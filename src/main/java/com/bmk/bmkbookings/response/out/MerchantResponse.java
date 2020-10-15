package com.bmk.bmkbookings.response.out;

import com.bmk.bmkbookings.response.in.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantResponse {
    Long merchantId;
    String merchantName;
    String merchantType;
    String phone;
    double distance;
    ImageResponse[] imageArr;
    String email;
    String phoneNumber;
}
