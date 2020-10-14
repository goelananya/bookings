package com.bmk.bmkbookings.repo;

import com.bmk.bmkbookings.bo.PaymentBo;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepo extends CrudRepository<PaymentBo, Long> {
}
