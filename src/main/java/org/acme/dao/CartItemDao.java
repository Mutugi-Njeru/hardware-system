package org.acme.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.acme.models.CartItem;
import org.acme.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

@ApplicationScoped
public class CartItemDao {
    private final AgroalDataSource ads;
    private static final Logger logger = LoggerFactory.getLogger(CartItemDao.class);

    @Inject
    public CartItemDao(AgroalDataSource ads) {
        this.ads = ads;
    }
    //check if any active cart exists if yes get cartId
    // if no create new cart
    // add item to cartItem

    public Optional<Integer> isThereActiveCart(int userId) {
        String query = "SELECT cart_id FROM cart WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getInt("cart_id"));
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return Optional.empty();
    }

    public int createCart(int userId) {
        String query = "INSERT INTO cart (user_id, status) VALUES (?,?)";
        int cartId = 0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, "ACTIVE");
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                cartId = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return cartId;
    }

    public int addItemToCart(CartItem cartItem, int cartId, Double price) {
        String query = "INSERT INTO cart_item (cart_id, product_id, quantity, price) VALUES (?,?,?,?)";
        int cartItemId = 0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, cartId);
            preparedStatement.setInt(2, cartItem.productId());
            preparedStatement.setInt(3, cartItem.quantity());
            preparedStatement.setDouble(4, price);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                cartItemId = resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return cartItemId;
    }

    public Double getProductPrice(int productId) {
        String query = "SELECT price_per_unit FROM products WHERE product_id = ?";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("price_per_unit");
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }

        return null;
    }

    public JsonObject getCartItems(int userId) {
        String query = """
                SELECT c.cart_id, c.user_id, c.status, c.created_at, ci.cart_item_id, ci.product_id, p.name AS product_name, ci.quantity, ci.price,
                    (ci.quantity * ci.price) AS subtotal,
                    total_cart.total_price
                FROM cart c JOIN cart_item ci ON c.cart_id = ci.cart_id
                JOIN products p ON ci.product_id = p.product_id
                JOIN ( SELECT cart_id, SUM(quantity * price) AS total_price FROM cart_item GROUP BY cart_id)
                 AS total_cart ON c.cart_id = total_cart.cart_id
                 WHERE c.user_id=? AND c.status='ACTIVE'
                 ORDER BY c.cart_id, ci.cart_item_id""";
        JsonObjectBuilder cartBuilder = Json.createObjectBuilder();
        var products = Json.createArrayBuilder();
        boolean cartInfoAdded = false;

        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (!cartInfoAdded) {
                    cartBuilder.add("cartId", resultSet.getInt("cart_id")).add("userId", resultSet.getInt("user_id")).add("status", resultSet.getString("status")).add("createdAt", String.valueOf(resultSet.getTimestamp("created_at"))).add("totalPrice", resultSet.getDouble("total_price"));
                    cartInfoAdded = true;
                }
                JsonObject item = Json.createObjectBuilder().add("cartItemId", resultSet.getInt("cart_item_id")).add("productId", resultSet.getInt("product_id")).add("productName", resultSet.getString("product_name")).add("quantity", resultSet.getInt("quantity")).add("price", resultSet.getDouble("price")).add("subTotal", resultSet.getDouble("subtotal")).build();
                products.add(item);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        cartBuilder.add("items", products);
        return cartBuilder.build();
    }
    public boolean updateCartStatus (int cartId){
        String query="UPDATE cart SET status='ORDERED' WHERE cart_id=? AND status=?";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cartId);
            preparedStatement.setString(2, "ACTIVE");
            return preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return false;
    }


}
