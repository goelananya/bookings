package com.bmk.bmkbookings.request.out;

import com.bmk.bmkbookings.bo.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class FcmRequest {
    String to;
    Map<String, String> data;
    Notification notification;
}