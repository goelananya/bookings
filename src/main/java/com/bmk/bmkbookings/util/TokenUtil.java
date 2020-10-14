package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.xml.bind.DatatypeConverter;

public class TokenUtil {
    public static String getUserId(String jwt) throws UnauthorizedUserException {
        try {
            return getClaim(jwt).getId();
        } catch(Exception exp){
            throw new UnauthorizedUserException();
        }
    }

    public static String getUserType(String jwt) {
        try {
            return getClaim(jwt).getSubject();
        } catch(Exception exp){
            return null;
        }
    }

    public static Claims getClaim(String jwt){
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(System.getenv("jwtSecret")))
                .parseClaimsJws(jwt).getBody();
    }

}