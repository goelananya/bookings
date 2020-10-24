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
    ImageResponse[] imageArr;
    String email;
    LocationBo location;
    String phoneNumber;
    double averageRating;
    int totalRatings;
}
