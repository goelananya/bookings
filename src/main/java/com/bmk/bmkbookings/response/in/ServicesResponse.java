package com.bmk.bmkbookings.response.in;

import com.bmk.bmkbookings.response.in.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicesResponse {
    String responseCode;
    String message;
    Service[] data;
}
