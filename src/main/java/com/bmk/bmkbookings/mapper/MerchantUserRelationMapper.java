package com.bmk.bmkbookings.mapper;

import com.bmk.bmkbookings.bo.MerchantUserRelation;
import com.bmk.bmkbookings.response.in.User;
import com.bmk.bmkbookings.response.out.MerchantRelationsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {})
public interface MerchantUserRelationMapper {

    @Mapping(source = "merchantId", target = "merchantId")
    @Mapping(source = "lastVisitDate", target = "lastVisitDate")
    @Mapping(source = "totalVisits", target = "totalVisits")
    @Mapping(source = "clientId", target = "client", qualifiedByName = "clientMapper")
    MerchantRelationsDTO toDto(MerchantUserRelation merchantUserRelation);

    @Named("clientMapper")
    static User clientMapper(Long clientId) {
        User user = new User();
        user.setStaticUserId(clientId);
        return user;
    }
}