package com.bmk.bmkbookings.util;

public class StringUtil {


    public static boolean isEmpty(String str) {
        return str==null||str.equals("");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
