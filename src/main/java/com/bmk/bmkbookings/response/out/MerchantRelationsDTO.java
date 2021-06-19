package com.bmk.bmkbookings.response.out;

import com.bmk.bmkbookings.response.in.User;
import lombok.Data;

import java.util.Date;

@Data
public class MerchantRelationsDTO {
    Long merchantId;
    User client;
    Date lastVisitDate;
    int totalVisits;
}