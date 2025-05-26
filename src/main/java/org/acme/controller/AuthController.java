package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.ruleEngine.engine.Engine;

import java.util.Base64;

@Path("auth")
public class AuthController {
    @Inject
    Engine engine;

    @POST
    @Path("/request")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(@HeaderParam("Authorization") String basicAuthHeader) {
        var authArray = new String(Base64.getDecoder().decode(basicAuthHeader.replace("Basic", "").trim())).split(":");
        var request = Json.createObjectBuilder()
                .add("username", authArray[0])
                .add("password", authArray[1])
                .add("requestType", RequestTypes.AUTHENTICATE_USER.name());
        return engine.routeRequest(request.build(), Modules.AUTH.name());
    }
}
