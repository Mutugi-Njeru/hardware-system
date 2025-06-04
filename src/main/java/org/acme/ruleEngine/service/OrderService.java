package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.acme.dao.CartItemDao;
import org.acme.dao.OrderDao;
import org.acme.records.ServiceResponse;
import org.acme.utility.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrderService {
    private final OrderDao orderDao;
    private final CartItemDao cartItemDao;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Inject
    public OrderService(OrderDao orderDao, CartItemDao cartItemDao) {
        this.orderDao = orderDao;
        this.cartItemDao = cartItemDao;
    }

    @Transactional
    public ServiceResponse addOrder(JsonObject object) {
        int userId = object.getInt("userId");
        int paymentMethodId = object.getInt("paymentMethodId");

        JsonObject message = cartItemDao.getCartItems(userId);

        int cartId = message.getInt("cartId");
        double totalPrice = message.getJsonNumber("totalPrice").doubleValue();
        String code = Util.generateCode();

        JsonArray items = message.getJsonArray("items");
        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.getJsonObject(i);
            int productId = item.getInt("productId");
            int quantity = item.getInt("quantity");
            String name = item.getString("productName");

            if (!orderDao.isQuantityAvailable(quantity, productId)) {
                return new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, Json.createObjectBuilder().add("quantity unavailable for", name).build());
            }

            boolean isUpdated = orderDao.updateQuantityRemaining(quantity, productId);
            if (!isUpdated) {
                return new ServiceResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), false, Json.createObjectBuilder().add("error", "Failed to update stock for product: " + name).build());
            }

        }
        int orderId = orderDao.createOrder(userId, cartId, "PENDING", totalPrice, paymentMethodId, code);

        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.getJsonObject(i);
            int productId = item.getInt("productId");
            int quantity = item.getInt("quantity");
            double price = item.getJsonNumber("price").doubleValue();
            double subTotal = item.getJsonNumber("subTotal").doubleValue();
            orderDao.createOrderItem(orderId, productId, quantity, price, subTotal);
        }
        boolean cartUpdated = cartItemDao.updateCartStatus(cartId);
        if (!cartUpdated) {
            return new ServiceResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), false, Json.createObjectBuilder().add("error", "Failed to update cart status").build());
        }

        return new ServiceResponse(Response.Status.OK.getStatusCode(), true, Json.createObjectBuilder().add("message", "Order placed successfully").add("orderCode", code).build());
    }

}
