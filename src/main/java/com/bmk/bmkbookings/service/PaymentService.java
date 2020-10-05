package com.bmk.bmkbookings.service;

import com.bmk.bmkbookings.bo.PaymentBo;
import com.bmk.bmkbookings.repo.PaymentRepo;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public static PaymentRepo paymentRepo;

    public PaymentService(PaymentRepo paymentRepo){
        this.paymentRepo = paymentRepo;
    }

    public PaymentBo addPayment(PaymentBo paymentBo){
        return paymentRepo.save(paymentBo);
    }
}
