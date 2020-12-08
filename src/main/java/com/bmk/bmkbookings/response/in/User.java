package com.bmk.bmkbookings.response.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    Long staticUserId;
    String email;
    String password;
    String name;
    String dateOfBirth;
    String gender;
    String phone;
    String userType;
    String deviceId;
}