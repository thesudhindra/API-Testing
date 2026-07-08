package com.playground.banking.security;

import com.playground.banking.identity.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long ttlSeconds;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${playground.jwt.issuer}") String issuer,
            @Value("${playground.jwt.ttl-seconds}") long ttlSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public String createToken(UserEntity user) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getId())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .claim("tenant_id", user.getTenantId())
                .claim("username", user.getUsername())
                .claim("roles", roles);

        if (user.getPartyId() != null) {
            builder.claim("party_id", user.getPartyId());
        }

        JwsHeader header = JwsHeader.with(() -> "HS256").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, builder.build())).getTokenValue();
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }
}
