package org.acme.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class OrderDao {
    private final Logger logger = LoggerFactory.getLogger(OrderDao.class);
    private final AgroalDataSource ads;

    @Inject
    public OrderDao(AgroalDataSource ads) {
        this.ads = ads;
    }
    //confirm if quality is available with productId

    public boolean isQuantityAvailable(int quantity, int productId) {
        String query = "SELECT COUNT(*) FROM products WHERE quantity_available >= ? AND product_id = ?";

        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return false;
    }

    public int createOrder(int userId, int cartId, String status, Double totalAmount, int paymentMethod, String orderCode) {
        String query = "INSERT INTO orders (user_id, cart_id, status, total_amount, payment_method, code) VALUES (?,?,?,?,?,?)";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, cartId);
            preparedStatement.setString(3, status);
            preparedStatement.setDouble(4, totalAmount);
            preparedStatement.setInt(5, paymentMethod);
            preparedStatement.setString(6, orderCode);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return 0;
    }

    public int createOrderItem(int orderId, int productId, int quantity, Double price, Double subTotal) {
        String query = "INSERT INTO order_item (order_id, product_id, quantity, price, subtotal) VALUES (?,?,?,?,?)";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setDouble(4, price);
            preparedStatement.setDouble(5, subTotal);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return 0;
    }

    //update quantity after order placement
    public boolean updateQuantityRemaining(int quantity, int productId) {
        String query = "UPDATE products SET quantity_available = (quantity_available - ?) WHERE product_id = ? AND quantity_available >= ?";

        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantity);

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return false;
    }


}
