package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
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

@Path("user")
public class UserController {
    private final Engine engine;

    @Inject
    public UserController(Engine engine) {
        this.engine = engine;
    }

    @POST
    @Path("create-superadmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSuperAdmin(InputStream inputStream) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.CREATE_SUPER_ADMIN.name())
                .build();
        return engine.routeRequest(requestJson, Modules.USER.name());
    }
    @POST
    @Path("create-admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdmin(InputStream inputStream) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.CREATE_ADMIN.name())
                .build();
        return engine.routeRequest(requestJson, Modules.USER.name());
    }
    @POST
    @Path("create-client")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createClient(InputStream inputStream) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream))
                .add("requestType", RequestTypes.CREATE_CLIENT.name())
                .build();
        return engine.routeRequest(requestJson, Modules.USER.name());
    }
}
