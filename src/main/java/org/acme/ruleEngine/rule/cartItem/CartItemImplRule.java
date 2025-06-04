package org.acme.ruleEngine.rule.cartItem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.models.CartItem;
import org.acme.models.Product;
import org.acme.ruleEngine.interfaces.ServiceRule;
import org.acme.ruleEngine.service.BeanValidatorService;
import org.acme.ruleEngine.service.CartItemService;
import org.acme.utility.Util;

import java.util.List;

@ApplicationScoped
public class CartItemImplRule implements ServiceRule {
    private final CartItemService cartItemService;
    private final BeanValidatorService validatorService;

    @Inject
    public CartItemImplRule(CartItemService cartItemService, BeanValidatorService validatorService) {
        this.cartItemService = cartItemService;
        this.validatorService = validatorService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.CART_ITEM.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)) {
            case ADD_TO_CART:
                CartItem cartItem = new CartItem(requestBody);
                List<String> violations = validatorService.validateDTO(cartItem);

                if (!violations.isEmpty()) {
                    return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
                }
                return Util.buildResponse(cartItemService.addItemToCart(cartItem));
            case GET_CART_ITEMS:
                return Util.buildResponse(cartItemService.getCartItems(requestBody.getInt("userId")));
            default:
                throw new IllegalArgumentException("Unhandled request type: " + requestType);
        }
    }
}
