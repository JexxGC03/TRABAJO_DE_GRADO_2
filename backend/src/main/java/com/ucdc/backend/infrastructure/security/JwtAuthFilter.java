package com.ucdc.backend.infrastructure.security;

import com.ucdc.backend.domain.model.User;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.JwtProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProviderPort jwt;
    private final UserRepository users;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(req, res); // públicas siguen; privadas responderán 401
            return;
        }

        String token = auth.substring(7);
        try {
            var userIdOpt = jwt.parseUserIdFromAccess(token);
            if (userIdOpt.isEmpty()) {
                SecurityContextHolder.clearContext();
                chain.doFilter(req, res);
                return;
            }

            UUID userId = userIdOpt.get();
            var userOpt = users.findById(userId); // ajusta al nombre real si difiere
            if (userOpt.isEmpty()) {
                SecurityContextHolder.clearContext();
                chain.doFilter(req, res);
                return;
            }

            User user = userOpt.get();
            var authentication = new UsernamePasswordAuthenticationToken(
                    user, null, toAuthorities(user)
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext(); // token malformado, expirado, etc.
        }

        chain.doFilter(req, res);
    }

    private Collection<? extends GrantedAuthority> toAuthorities(User user) {
        // Adapta a tu modelo: getRole().name() / role().name() / getRoleString()
        String role = user.role().name();
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(authority));
    }
}
