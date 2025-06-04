package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.acme.dao.CartItemDao;
import org.acme.models.CartItem;
import org.acme.records.ServiceResponse;

import java.util.Optional;

@ApplicationScoped
public class CartItemService {
    private final CartItemDao cartItemDao;

    @Inject
    public CartItemService(CartItemDao cartItemDao) {
        this.cartItemDao = cartItemDao;
    }

    @Transactional
    public ServiceResponse addItemToCart(CartItem cartItem) {
        int cartId = cartItemDao.isThereActiveCart(cartItem.userId()).orElseGet(() -> cartItemDao.createCart(cartItem.userId()));

        Double productPrice = cartItemDao.getProductPrice(cartItem.productId());


        int cartItemId = cartItemDao.addItemToCart(cartItem, cartId, productPrice);

        if (cartItemId > 0) {
            return new ServiceResponse(Response.Status.OK.getStatusCode(), true, "Item added to cart");
        } else {
            return new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "Cannot add item to cart");
        }
    }
    public ServiceResponse getCartItems(int userId){
        JsonObject cartItems = cartItemDao.getCartItems(userId);
        return (!cartItems.isEmpty())
                ? new ServiceResponse(Response.Status.OK.getStatusCode(), true, cartItems)
                : new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "No items found on cart. Please add some");
    }

}
