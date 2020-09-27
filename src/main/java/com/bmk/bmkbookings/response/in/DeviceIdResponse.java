package com.bmk.bmkbookings.response.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceIdResponse {
    String responseCode;
    String message;
    String deviceId;
}
