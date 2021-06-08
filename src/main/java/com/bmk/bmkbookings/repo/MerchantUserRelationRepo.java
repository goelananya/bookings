package com.bmk.bmkbookings.repo;

import com.bmk.bmkbookings.bo.MerchantUserRelation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantUserRelationRepo extends CrudRepository<MerchantUserRelation, Long> {

    Optional<MerchantUserRelation> findByMerchantIdAndClientId(Long merchantId, Long clientId);

    List<MerchantUserRelation> findByMerchantId(Long merchantId);
}
