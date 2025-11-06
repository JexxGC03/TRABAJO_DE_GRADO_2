package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.dto.user.GetMyProfileQuery;
import com.ucdc.backend.application.usecase.user.ChangePasswordUseCase;
import com.ucdc.backend.application.usecase.user.GetMyProfileUseCase;
import com.ucdc.backend.application.usecase.user.UpdateProfileUseCase;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.infrastructure.web.dto.user.ChangePasswordRequest;
import com.ucdc.backend.infrastructure.web.dto.user.ChangePasswordResponse;
import com.ucdc.backend.infrastructure.web.dto.user.ProfileResponse;
import com.ucdc.backend.infrastructure.web.dto.user.UpdateProfileRequest;
import com.ucdc.backend.infrastructure.web.mapper.AuthApiMapper;
import com.ucdc.backend.infrastructure.web.mapper.UserApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ChangePasswordUseCase changePassword;
    private final GetMyProfileUseCase getMyProfile;
    private final UpdateProfileUseCase updateProfile;
    private final AuthApiMapper mapperAuth;
    private final UserApiMapper mapperUser;

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @AuthenticationPrincipal ClientUser me,
            @RequestBody @Valid ChangePasswordRequest req
    ) {
        var cmd = mapperAuth.toCommand(me.id(), req);
        var result = changePassword.handle(cmd);
        var body = new ChangePasswordResponse(
                "Contraseña actualizada correctamente",
                result.updatedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
        return ResponseEntity.ok(body);
    }


    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me(@AuthenticationPrincipal ClientUser me) {
        var result = getMyProfile.handle(new GetMyProfileQuery(me.id()));
        return ResponseEntity.ok(mapperUser.toResponse(result));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMe( @AuthenticationPrincipal ClientUser auth, @RequestBody @Valid UpdateProfileRequest req) {
        var cmd = mapperUser.toUpdateCommand(auth.id(), req);
        var result = updateProfile.update(cmd);
        return ResponseEntity.ok(mapperUser.toResponse(result));
    }
}
