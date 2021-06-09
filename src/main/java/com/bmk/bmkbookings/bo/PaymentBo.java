package com.bmk.bmkbookings.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PaymentBo {
    @Id
    String orderId;
    String razorpay_payment_id;
    String razorpay_order_id;
    String razorpay_signature;
    boolean isSuccess;
}
