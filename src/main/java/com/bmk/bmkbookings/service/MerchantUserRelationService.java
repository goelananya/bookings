package com.bmk.bmkbookings.service;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.MerchantUserRelation;
import com.bmk.bmkbookings.mapper.MerchantUserRelationMapper;
import com.bmk.bmkbookings.repo.MerchantUserRelationRepo;
import com.bmk.bmkbookings.response.out.MerchantRelationsDTO;
import com.bmk.bmkbookings.util.RestClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MerchantUserRelationService {
    public final MerchantUserRelationRepo merchantUserRelationRepo;
    private static RestClient restClient;
    private static MerchantUserRelationMapper merchantUserRelationMapper;

    public MerchantUserRelationService(MerchantUserRelationRepo merchantUserRelationRepo, RestClient restClient, MerchantUserRelationMapper merchantUserRelationMapper) {
        this.merchantUserRelationRepo = merchantUserRelationRepo;
        this.restClient = restClient;
        this.merchantUserRelationMapper = merchantUserRelationMapper;
    }

    public void save(Booking booking) {
        Optional<MerchantUserRelation> existingRelation = merchantUserRelationRepo.findByMerchantIdAndClientId(booking.getMerchantId(), booking.getClientId());
        MerchantUserRelation merchantUserRelation;
        if(existingRelation.isPresent()) {
            merchantUserRelation = existingRelation.get();
            merchantUserRelation.setTotalVisits(merchantUserRelation.getTotalVisits() + 1);
            merchantUserRelation.setLastVisitDate(booking.getDate());
        } else {
            merchantUserRelation = MerchantUserRelation.builder().clientId(booking.getClientId())
                    .merchantId(booking.getMerchantId()).totalVisits(1).lastVisitDate(booking.getDate()).build();
        }
        merchantUserRelationRepo.save(merchantUserRelation);
    }

    public List<MerchantRelationsDTO>  getRelationships(Long merchantId) {
        List<MerchantRelationsDTO> relations = merchantUserRelationRepo.findByMerchantId(merchantId).stream().map(merchantUserRelationMapper::toDto).collect(Collectors.toList());
        relations.forEach(x -> x.setClient(restClient.getUser(x.getClient().getStaticUserId())));
        return relations;
    }

    public List<MerchantRelationsDTO> getRelationships(Long merchantId, Long clientId) {
        List<MerchantRelationsDTO> relations = merchantUserRelationRepo.findByMerchantIdAndClientId(merchantId, clientId).stream().map(merchantUserRelationMapper::toDto).collect(Collectors.toList());
        relations.forEach(x -> x.setClient(restClient.getUser(x.getClient().getStaticUserId())));
        return relations;
    }

}