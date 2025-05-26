package org.acme.ruleEngine.engine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.acme.ruleEngine.interfaces.ServiceRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Engine {
    private static final Logger logger = LoggerFactory.getLogger(Engine.class);
    @Inject
    private Instance<ServiceRule> rules;

    public Response routeRequest(JsonObject request, String module) {
        String requestType = request.getString("requestType");
        for (ServiceRule rule : rules) {
            if (rule.matches(module)) {
                return Response.ok(rule.apply(request).toString()).build();
            }
        }
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("Unknown request module. Please try again.")
                .build();
    }
}
