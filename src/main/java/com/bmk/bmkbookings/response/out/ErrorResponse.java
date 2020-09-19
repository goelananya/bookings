package com.bmk.bmkbookings.response.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    String responseCode;
    String response;
}
