package com.ucdc.backend.infrastructure.security;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ucdc.backend.domain.model.User;
import com.ucdc.backend.domain.security.JwtProviderPort;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class JwtProviderAdapter implements JwtProviderPort {
    private final JwtProperties props;

    public JwtProviderAdapter(JwtProperties props) { this.props = props; }

    @Override
    public String generateAccessToken(User user, long ttlSeconds) {
        return sign(user, ttlSeconds > 0 ? ttlSeconds : props.accessTtlSeconds());
    }

    @Override
    public String generateRefreshToken(User user, long ttlSeconds) {
        // Refresh más largo, menos claims
        var now = Instant.now();
        var exp = now.plusSeconds(ttlSeconds > 0 ? ttlSeconds : props.refreshTtlSeconds());
        var claims = new JWTClaimsSet.Builder()
                .issuer(props.issuer())
                .audience(props.audience())
                .subject(user.id().toString())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .claim("typ", "refresh")
                .build();
        return signClaims(claims);
    }

    private String sign(User user, long ttl) {
        var now = Instant.now();
        var exp = now.plusSeconds(ttl);
        var claims = new JWTClaimsSet.Builder()
                .issuer(props.issuer())
                .audience(props.audience())
                .subject(user.id().toString())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .claim("typ", "access")
                .claim("role", user.role().name())
                .build();
        return signClaims(claims);
    }

    private String signClaims(JWTClaimsSet claims) {
        try {
            var header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
            var jwt = new SignedJWT(header, claims);
            jwt.sign(new MACSigner(props.secret().getBytes()));
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("JWT signing error", e);
        }
    }

    @Override
    public Optional<java.util.UUID> parseUserIdFromAccess(String jwt) {
        try {
            var signed = SignedJWT.parse(jwt);
            var verifier = new MACVerifier(props.secret().getBytes());
            if (!signed.verify(verifier)) return Optional.empty();
            var claims = signed.getJWTClaimsSet();
            if (!"access".equals(claims.getStringClaim("typ"))) return Optional.empty();
            if (claims.getExpirationTime().toInstant().isBefore(Instant.now())) return Optional.empty();
            return Optional.of(UUID.fromString(claims.getSubject()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
