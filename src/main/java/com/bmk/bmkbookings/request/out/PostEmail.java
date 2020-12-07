package com.bmk.bmkbookings.request.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostEmail {
    String toEmail;
    String subject;
    String content;
}
