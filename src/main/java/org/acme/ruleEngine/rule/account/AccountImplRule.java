package org.acme.ruleEngine.rule.account;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.models.Account;
import org.acme.ruleEngine.interfaces.ServiceRule;
import org.acme.ruleEngine.service.AccountService;
import org.acme.ruleEngine.service.BeanValidatorService;
import org.acme.utility.Util;

import java.util.List;

@ApplicationScoped
public class AccountImplRule implements ServiceRule {
    private final AccountService accountService;
    private final BeanValidatorService validatorService;

    @Inject
    public AccountImplRule(AccountService accountService, BeanValidatorService validatorService) {
        this.accountService = accountService;
        this.validatorService = validatorService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.ACCOUNT.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)) {
            case CREATE_ACCOUNT:
                Account account = new Account(requestBody);
                List<String> violations = validatorService.validateDTO(account);

                if (!violations.isEmpty()) {
                    return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
                }
                return Util.buildResponse(accountService.createAccount(account));
            case GET_ACCOUNTS:
                return Util.buildResponse(accountService.getAllAccounts());
            default:
                throw new IllegalArgumentException("Unhandled request type: " + requestType);
        }
    }
}
