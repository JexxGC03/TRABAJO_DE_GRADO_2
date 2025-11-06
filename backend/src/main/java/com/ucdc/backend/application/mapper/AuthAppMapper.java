package com.ucdc.backend.application.mapper;

import com.ucdc.backend.application.dto.auth.LoginResult;
import com.ucdc.backend.application.dto.auth.RegisterUserCommand;
import com.ucdc.backend.application.dto.auth.RegisterUserResult;
import com.ucdc.backend.application.dto.user.UpdateProfileCommand;
import com.ucdc.backend.application.dto.user.UpdateProfileResult;
import com.ucdc.backend.domain.enums.Role;
import com.ucdc.backend.domain.enums.UserStatus;
import com.ucdc.backend.domain.model.AdminUser;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.model.User;
import com.ucdc.backend.domain.value.CitizenId;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.domain.value.Phone;
import com.ucdc.backend.domain.value.ServiceNumber;
import org.mapstruct.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Mapper de la capa de aplicación para AUTH.
 * - Convierte comandos de registro/perfil a modelos de dominio.
 * - Convierte modelos de dominio a resultados (Register/Login/UpdateProfile).
 */
@Mapper(componentModel = "spring")
public interface AuthAppMapper {
    /* ========= Register: Command -> Domain ========= */
    /**
     * Construye el subtipo correcto (AdminUser/ClientUser).
     * Evita que MapStruct intente instanciar la clase abstracta User.
     */
    default User toDomain(RegisterUserCommand cmd) {
        final OffsetDateTime now = OffsetDateTime.now();
        final UUID id = UUID.randomUUID();

        final Email email         = new Email(cmd.email());
        final CitizenId citizenId = new CitizenId(cmd.citizenId());
        final ServiceNumber svc   = new ServiceNumber(cmd.serviceNumber());
        final Phone phone         = cmd.phone() != null ? new Phone(cmd.phone()) : null;

        if (Boolean.TRUE.equals(cmd.admin())) {
            // Si tu constructor no recibe Role (porque la subclase lo fija), elimina el argumento Role.*
            return new AdminUser(
                    id,
                    cmd.name(),
                    email,
                    citizenId,
                    svc,
                    phone,
                    UserStatus.ACTIVE,
                    now,
                    now
            );
        } else {
            return new ClientUser(
                    id,
                    cmd.name(),
                    email,
                    citizenId,
                    svc,
                    phone,
                    UserStatus.ACTIVE,
                    now,
                    now
            );
        }
    }

    /* ========= Login ========= */
    default LoginResult toLoginResult(String tokenType,
                                      String accessToken,
                                      int accessTtlSeconds,
                                      String refreshToken,
                                      OffsetDateTime refreshExp,
                                      User user) {
        return new LoginResult(
                tokenType,
                accessToken,
                accessTtlSeconds,
                refreshToken,
                refreshExp,
                toUserSummary(user)
        );
    }

    /**
     * Requiere que hayas definido en LoginResult:
     *   public record UserSummary(UUID id, String fullName, String email, String role, String status) {}
     * Si lo tienes como clase separada, cambia el tipo de retorno y el 'new' abajo.
     */
    default LoginResult.UserSummary toUserSummary(User u) {
        return new LoginResult.UserSummary(
                u.id(),
                u.fullName(),
                u.email().value(),
                u.role().name(),
                u.status().name()
        );
    }

    /* ========= Register: Domain -> Result ========= */
    // Ajusta el constructor de RegisterUserResult a tu DTO real
    default RegisterUserResult toRegisterResult(User u) {
        return new RegisterUserResult(
                u.id(),
                u.fullName(),
                u.email().value(),
                u.role().name(),
                u.status().name()
        );
    }

    /* ========= Update Profile ========= */
    /**
     * Aplica cambios inmutables devolviendo una NUEVA instancia del subtipo correcto.
     * Si prefieres mutar (tienes setters), adapta en consecuencia.
     */
    default User apply(UpdateProfileCommand cmd, User current) {
        final String fullName = cmd.name() != null ? cmd.name() : current.fullName();
        final Phone phone     = cmd.phone() != null ? new Phone(cmd.phone()) : current.phone();
        final OffsetDateTime now = OffsetDateTime.now();

        if (current instanceof AdminUser a) {
            return new AdminUser(
                    a.id(),
                    fullName,
                    a.email(),
                    a.citizenId(),
                    a.serviceNumber(),
                    phone,
                    a.status(),
                    a.createdAt(),
                    now
            );
        } else if (current instanceof ClientUser c) {
            return new ClientUser(
                    c.id(),
                    fullName,
                    c.email(),
                    c.citizenId(),
                    c.serviceNumber(),
                    phone,
                    c.status(),           // o Role.CLIENT si tu ctor lo exige
                    c.createdAt(),
                    now
            );
        }
        // Si en el futuro tienes más subtipos:
        throw new IllegalStateException("Unsupported user subtype: " + current.getClass().getName());
    }

    // Proyección a resultado de Update Profile (ajusta a tu DTO real)
    default UpdateProfileResult toUpdateProfileResult(User u) {
        return new UpdateProfileResult(
                u.fullName(),
                u.email().value(),
                u.phone() != null ? u.phone().value() : null
        );
    }
}
