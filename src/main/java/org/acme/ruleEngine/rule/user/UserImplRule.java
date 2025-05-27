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

        RequestTypes type;
        try {
            type = RequestTypes.valueOf(requestType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }

        switch (type) {
            case CREATE_SUPER_ADMIN:
            case CREATE_ADMIN:
            case CREATE_CLIENT:
                User user = new User(requestBody);
                List<String> violations = validatorService.validateDTO(user);

                if (!violations.isEmpty()) {
                    return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
                }

                return switch (type) {
                    case CREATE_SUPER_ADMIN -> Util.buildResponse(userService.createSuperAdmin(user));
                    case CREATE_ADMIN -> Util.buildResponse(userService.createAdmin(user));
                    case CREATE_CLIENT -> Util.buildResponse(userService.createClient(user));
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                };
            // Example future case that just needs an integer from the request
//            case UPDATE_USER_STATUS:
//                int userId = requestBody.getInt("userId", -1);
//                if (userId == -1) {
//                    return Json.createObjectBuilder().add("message", "Missing userId").build();
//                }
//                return Util.buildResponse(userService.updateUserStatus(userId));

            default:
                throw new IllegalArgumentException("Unhandled request type: " + requestType);
        }
    }

}
