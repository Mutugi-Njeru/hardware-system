package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import org.acme.cipher.Sha256Hasher;
import org.acme.dao.AuthenticationDao;
import org.acme.records.Authentication;
import org.acme.records.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuthenticationService {
    private final AuthenticationDao authenticationDao;
    private final TokenBuilderService tokenBuilderService;
    private final Sha256Hasher sha256Hasher;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    @Inject
    public AuthenticationService(AuthenticationDao authenticationDao, TokenBuilderService tokenBuilderService, Sha256Hasher sha256Hasher) {
        this.authenticationDao = authenticationDao;
        this.tokenBuilderService = tokenBuilderService;
        this.sha256Hasher = sha256Hasher;
    }

    public ServiceResponse authenticateUser(String username, String password) {
        String hashPassword= sha256Hasher.createHashText(password);
        Authentication authentication = authenticationDao.authenticate(username, hashPassword);
        if (!authentication.isAuthenticated()) {
            return new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "Invalid credential. please try again");
        }
        LOGGER.info("user-------->{}", authentication);

        String token = tokenBuilderService.generateAccessToken(authentication);
        var finalResponse = Json.createObjectBuilder().add("accessToken", token).build();
        return new ServiceResponse(Response.Status.OK.getStatusCode(), true, finalResponse);
    }


    public ServiceResponse verifyToken(String authHeaderValue) {

        if (!authHeaderValue.startsWith("Bearer ")) {
            return new ServiceResponse(Response.Status.FORBIDDEN.getStatusCode(), false, "Invalid Request. The Bearer structure provided is incorrect ");
        }

        var token = authHeaderValue.substring(7);
        Authentication authentication = tokenBuilderService.decodeAccessToken(token);
        LOGGER.info("Authentic------->  {}", authentication);

        return (authentication.isAuthenticated()) ? new ServiceResponse(Response.Status.OK.getStatusCode(), true, "User authenticated successfully") : new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "Authentication failed. Token provided could not be verified");
    }

    public Authentication getAuthenticationFromToken(String authHeaderValue) {
        if (!authHeaderValue.startsWith("Bearer ")) {
            return new Authentication(false, null, null);
        }
        var token = authHeaderValue.substring(7);
        return tokenBuilderService.decodeAccessToken(token);
    }



}
