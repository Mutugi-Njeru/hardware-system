package org.acme.ruleEngine.rule.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.models.CartItem;
import org.acme.ruleEngine.interfaces.ServiceRule;
import org.acme.ruleEngine.service.OrderService;
import org.acme.utility.Util;

import java.util.List;

@ApplicationScoped
public class OrderImplRule implements ServiceRule {
    private final OrderService orderService;

    @Inject
    public OrderImplRule(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.ORDER.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)) {
            case ADD_ORDER:
                return Util.buildResponse(orderService.addOrder(requestBody));

            default:
                throw new IllegalArgumentException("Unhandled request type: " + requestType);
        }
    }
}
