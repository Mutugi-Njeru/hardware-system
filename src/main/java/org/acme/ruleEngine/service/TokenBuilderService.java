package org.acme.ruleEngine.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.records.Authentication;
import org.acme.utility.Util;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class TokenBuilderService {
    public String generateAccessToken(Authentication authentication) {
        long milliseconds = (new Date().getTime()) + (3600 * 1000);
        Date expireDate = new Date(milliseconds);
        return JWT.create()
                .withClaim("createdAt", Util.getTimestamp())
                .withClaim("username", authentication.username())
                .withClaim("groups", authentication.roles())
                .withIssuer("klaus")
                .withClaim("expiresIn", 3600)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC512("ninja"));
    }

    public Authentication decodeAccessToken(String accessToken) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512("ninja"))
                .withIssuer("klaus")
                .acceptExpiresAt(3600)
                .build();
        DecodedJWT decodedJWT = verifier.verify(accessToken);
        String username = decodedJWT.getClaims().get("username").asString();
        List<String> roles = decodedJWT.getClaims().get("groups").asList(String.class);
        return new Authentication(true, username, roles);
    }
}
