package com.bmk.bmkbookings.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchantUserRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    Long merchantId;
    Long clientId;
    Date lastVisitDate;
    int totalVisits;
}