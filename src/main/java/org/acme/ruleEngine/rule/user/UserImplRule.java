package org.acme.ruleEngine.rule.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.models.User;
import org.acme.ruleEngine.interfaces.ServiceRule;
import org.acme.ruleEngine.service.BeanValidatorService;
import org.acme.ruleEngine.service.UserService;
import org.acme.utility.Util;

import java.util.List;

@ApplicationScoped
public class UserImplRule implements ServiceRule {
    private final UserService userService;
    private final BeanValidatorService validatorService;

    @Inject
    public UserImplRule(UserService userService, BeanValidatorService validatorService) {
        this.userService = userService;
        this.validatorService = validatorService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.USER.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");
        User user = new User(requestBody);
        List<String> violations = validatorService.validateDTO(user);

        if (!violations.isEmpty()) {
            return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
        }

        RequestTypes type;
        try {
            type = RequestTypes.valueOf(requestType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }

        switch (type) {
            case CREATE_SUPER_ADMIN:
                return Util.buildResponse(userService.createSuperAdmin(user));
            case CREATE_ADMIN:
                return Util.buildResponse(userService.createAdmin(user));
            case CREATE_CLIENT:
                return Util.buildResponse(userService.createClient(user));
            default:
                throw new IllegalArgumentException("Unhandled request type: " + requestType);
        }
    }
}
