package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.response.in.User;
import com.bmk.bmkbookings.response.out.BookingResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MailHelper {

    public static String merchantNewBookingTemplate = System.getProperty("user.dir")+"/src/main/resources/templates/mail/NewBookingMerchant.html";
    public static String clientNewBookingTemplate = System.getProperty("user.dir")+"/src/main/resources/templates/mail/NewBookingClient.html";
    public static Path mnbtPath = Path.of(merchantNewBookingTemplate);
    public static Path cnbtPath = Path.of(clientNewBookingTemplate);

    public static String setContentForMerchant(Booking booking, User merchant) throws IOException {
        String fileStr = Files.readString(mnbtPath);
        int x = fileStr.indexOf('?');
        fileStr = fileStr.substring(0, x)+"tempXXX"+fileStr.substring(x+1);
        x = fileStr.indexOf('?');
        fileStr = fileStr.substring(0, x)+merchant.getName()+fileStr.substring(x+1);
        x = fileStr.indexOf('?');
        fileStr = fileStr.substring(0, x)+booking.getDate().toString()+fileStr.substring(x+1);
        x = fileStr.indexOf('?');
        fileStr = fileStr.substring(0, x)+booking.getPayableAmount()/100.0+fileStr.substring(x+1);
        x = fileStr.indexOf("tempXXX");
        fileStr = fileStr.substring(0, x)+"?"+fileStr.substring(x+1);
        return fileStr;
    }

    public static String setContentForClient(Booking booking, User client) throws IOException {
        String fileStr = Files.readString(cnbtPath);
        int x = fileStr.indexOf('?');
        fileStr = fileStr.substring(0, x)+"tempXXX"+fileStr.substring(x+1);
        x = fileStr.indexOf('?');
        fileStr = fileStr.substring(0, x)+client.getName()+fileStr.substring(x+1);
        x = fileStr.indexOf("tempXXX");
        fileStr = fileStr.substring(0, x)+"?"+fileStr.substring(x+1);
        return fileStr;
    }
}