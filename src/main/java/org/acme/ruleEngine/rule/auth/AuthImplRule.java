package org.acme.ruleEngine.rule.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.ruleEngine.interfaces.ServiceRule;
import org.acme.ruleEngine.service.AuthenticationService;
import org.acme.utility.Util;

@ApplicationScoped
public class AuthImplRule implements ServiceRule {
    @Inject
    AuthenticationService authService;

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.AUTH.name()));
    }
    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)){
            case AUTHENTICATE_USER:
                return  Util.buildResponse(authService.authenticateUser(requestBody.getString("username"), requestBody.getString("password")));
            default:
                throw  new IllegalArgumentException("unexpected request type: " +requestType);
        }
    }
}
