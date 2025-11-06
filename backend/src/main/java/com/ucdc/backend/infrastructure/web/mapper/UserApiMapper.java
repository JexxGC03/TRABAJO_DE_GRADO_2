package com.ucdc.backend.infrastructure.web.mapper;

import com.ucdc.backend.application.dto.user.GetMyProfileResult;
import com.ucdc.backend.application.dto.user.UpdateProfileCommand;
import com.ucdc.backend.application.dto.user.UpdateProfileResult;
import com.ucdc.backend.infrastructure.web.dto.user.ProfileResponse;
import com.ucdc.backend.infrastructure.web.dto.user.UpdateProfileRequest;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper (componentModel = "spring")
public interface UserApiMapper {

    ProfileResponse toResponse(GetMyProfileResult r);

    // /me (PUT) → command
    default UpdateProfileCommand toUpdateCommand(UUID userId, UpdateProfileRequest req) {
        // Separar name/email/phone y pasar newPassword si viene
        return new UpdateProfileCommand(
                userId,
                req.fullName(),
                req.email(),
                req.phone(),
                (req.newPassword() != null && !req.newPassword().isBlank()) ? req.newPassword() : null
        );
    }

    // /me (PUT) ← result
    ProfileResponse toResponse(UpdateProfileResult result);
}
