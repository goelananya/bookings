package com.bmk.bmkbookings.response.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioItem {
    String category;
    int itemId;
    double price;
    double discountPercentage;
    Service service;
}
