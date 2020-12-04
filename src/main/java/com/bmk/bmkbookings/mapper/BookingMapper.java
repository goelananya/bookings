package com.bmk.bmkbookings.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {StringMapper.class})
@Named("BookingMappers")
public interface BookingMapper {
}
