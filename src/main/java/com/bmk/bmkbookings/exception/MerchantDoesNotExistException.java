package com.bmk.bmkbookings.exception;

public class MerchantDoesNotExistException extends Exception{
    public MerchantDoesNotExistException(Long merchantId) {
        super("Merchant with Id:"+merchantId+", does not exist.");
    }
}
