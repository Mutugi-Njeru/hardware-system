package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.ruleEngine.engine.Engine;
import org.acme.utility.Util;

import java.io.InputStream;

@Path("account")
public class AccountController {
    private final Engine engine;

    @Inject
    public AccountController(Engine engine) {
        this.engine = engine;
    }

    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(InputStream inputStream) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream)).add("requestType", RequestTypes.CREATE_ACCOUNT.name()).build();
        return engine.routeRequest(requestJson, Modules.ACCOUNT.name());
    }
    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts() {
        JsonObject requestJson = Json.createObjectBuilder().add("requestType", RequestTypes.GET_ACCOUNTS.name()).build();
        return engine.routeRequest(requestJson, Modules.ACCOUNT.name());
    }

}
