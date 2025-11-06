package com.ucdc.backend.infrastructure.web.mapper;

import com.ucdc.backend.domain.enums.Provider;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    default Provider toProvider(String name){
        return name==null? null : Provider.valueOf(name.trim().toUpperCase());
    }
    default String fromProvider(Provider p){
        return p==null? null : p.name();
    }
}
