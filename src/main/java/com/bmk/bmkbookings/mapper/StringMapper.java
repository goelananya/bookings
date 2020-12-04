package com.bmk.bmkbookings.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
@Named("StringMappers")
public interface StringMapper {
}
