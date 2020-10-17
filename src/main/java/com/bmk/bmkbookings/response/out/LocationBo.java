package com.bmk.bmkbookings.response.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationBo {
    Long locationId;
    double lat;
    double lng;
}
