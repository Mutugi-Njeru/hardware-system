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
@Path("cart")
public class CartController {
    private final Engine engine;

    @Inject
    public CartController(Engine engine) {
        this.engine = engine;
    }

    @POST
    @Path("add-product")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(InputStream inputStream) {
        JsonObject requestJson = Json.createObjectBuilder(Util.convertInputStreamToJson(inputStream)).add("requestType", RequestTypes.ADD_TO_CART.name()).build();
        return engine.routeRequest(requestJson, Modules.CART_ITEM.name());
    }
}
