package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.dto.auth.LoginResult;
import com.ucdc.backend.application.dto.auth.LogoutCommand;
import com.ucdc.backend.application.usecase.auth.*;
import com.ucdc.backend.infrastructure.web.dto.auth.*;
import com.ucdc.backend.infrastructure.web.mapper.AuthApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Registro, login, refresh y logout")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final LoginUseCase login;
    private final RefreshTokenUseCase refreshToken;
    private final LogoutUseCase logout;
    private final AuthApiMapper mapper;

    @Operation(summary = "Registro de usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest req) {
        // Validación simple de confirmación (si no la haces en dominio)
        if (!req.password().equals(req.confirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var cmd = mapper.toCommand(req);
        var result = registerUser.register(cmd); // devuelve RegisterUserResult
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest req) {
        var result = login.login(mapper.toCommand(req)); // TokenPairResult
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @Operation(summary = "Refrescar token")
    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        LoginResult result = refreshToken.refresh(mapper.toCommand(req));
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @Operation(summary = "Logout (revoca refresh token)")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearer) {
        logout.logout(new LogoutCommand(bearer));
        return ResponseEntity.noContent().build();
    }
}
