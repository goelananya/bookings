package com.bmk.bmkbookings.cache;

import com.bmk.bmkbookings.response.out.MerchantResponse;

import java.util.HashMap;
import java.util.Map;

public class MerchantCache {
    public static Map<Long, MerchantResponse> map = new HashMap<>();
}
