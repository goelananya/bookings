package com.bmk.bmkbookings.response.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioResponse {
    String responseCode;
    String message;
    Map<Integer, PortfolioItem> data;
}
