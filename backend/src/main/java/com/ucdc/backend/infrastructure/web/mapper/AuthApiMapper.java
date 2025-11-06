package com.ucdc.backend.infrastructure.web.mapper;

import com.ucdc.backend.application.dto.auth.*;
import com.ucdc.backend.infrastructure.web.dto.auth.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthApiMapper {

    // Requests -> Commands
    @Mapping(target = "name", source = "fullName")
    @Mapping(target = "citizenId", source = "citizenId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "serviceNumber", source = "serviceNumber")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "password", source = "password")
    RegisterUserCommand toCommand(RegisterUserRequest req);

    LoginCommand toCommand(LoginRequest req);

    RefreshTokenCommand toCommand(RefreshTokenRequest req);

    // Results -> Responses
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", expression = "java(result.role())")
    @Mapping(target = "status", expression = "java(result.status())")
    UserResponse toResponse(RegisterUserResult result);

    // LoginResult -> TokenPairResponse
    default TokenPairResponse toResponse(LoginResult result) {
        String tokenType = (result.tokenType() != null && !result.tokenType().isBlank())
                ? result.tokenType()
                : "Bearer";
        return new TokenPairResponse(
                result.accessToken(),
                result.refreshToken(),
                tokenType,
                // expiresIn del access token
                (long) result.accessTtl()
        );
    }
}
