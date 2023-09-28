package com.socialshop.backend.authservice.services.mapper;

import com.socialshop.backend.authservice.services.dtos.RegisterRequest;
import com.socialshop.backend.authservice.services.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(RegisterRequest request);
}
