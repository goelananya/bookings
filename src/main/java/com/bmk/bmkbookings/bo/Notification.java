package com.bmk.bmkbookings.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notification {
    String title;
    String body;
    String image;
}