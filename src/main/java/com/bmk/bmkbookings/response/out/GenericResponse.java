package com.bmk.bmkbookings.response.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericResponse {
    String responseCOde;
    String message;
    Object object;
}
