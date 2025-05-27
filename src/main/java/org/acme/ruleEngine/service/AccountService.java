package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.acme.dao.AccountDao;
import org.acme.models.Account;
import org.acme.records.ServiceResponse;

@ApplicationScoped
public class AccountService {
    private final AccountDao accountDao;

    @Inject
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public ServiceResponse createAccount (Account account){
        boolean isExist= accountDao.isAccountExist(account.registrationPin());
        if (!isExist){
            int accountId= accountDao.createAccount(account);
            return (accountId>1)
                    ? new ServiceResponse(Response.Status.OK.getStatusCode(), true, "Account created successfully")
                    : new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create account");

        }
        else return new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "Account already exists with that registration pin");
    }
    public ServiceResponse getAllAccounts (){
        JsonObject allAccounts = accountDao.getAllAccounts();
        return (!allAccounts.isEmpty())
                ? new ServiceResponse(Response.Status.OK.getStatusCode(), true, allAccounts)
                : new ServiceResponse(Response.Status.ACCEPTED.getStatusCode(), false, Json.createObjectBuilder().build());
    }
}
