package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.ruleEngine.engine.Engine;

@Path("order")
public class OrderController {
    private final Engine engine;

    @Inject
    public OrderController(Engine engine) {
        this.engine = engine;
    }

    @POST
    @Path("add/{userId}/{paymentMethodId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addOrder(@PathParam("userId") int userId, @PathParam("paymentMethodId") int paymentMethodId) {
        JsonObject requestJson = Json.createObjectBuilder().add("userId", userId).add("paymentMethodId", paymentMethodId).add("requestType", RequestTypes.ADD_ORDER.name()).build();
        return engine.routeRequest(requestJson, Modules.ORDER.name());
    }
}
