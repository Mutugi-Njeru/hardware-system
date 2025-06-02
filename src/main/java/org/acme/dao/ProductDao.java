package org.acme.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.acme.models.Product;
import org.acme.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class ProductDao {
    private final AgroalDataSource ads;
    private static final Logger logger = LoggerFactory.getLogger(ProductDao.class);

    @Inject
    public ProductDao(AgroalDataSource ads) {
        this.ads = ads;
    }

    public boolean isProductExist(int accountId, String name) {
        String query = "SELECT COUNT(*) FROM products WHERE account_id = ? AND name = ?";

        try (Connection connection = ads.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return false;
    }

    public int createProduct(Product product) {
        String query = "INSERT INTO products (account_id, unit_id, name, price_per_unit, quantity_available, image_url_1, image_url_2, status) VALUES (?,?,?,?,?,?,?,?)";
        int productId = 0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, product.accountId());
            preparedStatement.setInt(2, product.unitId());
            preparedStatement.setString(3, product.name());
            preparedStatement.setDouble(4, product.pricePerUnit());
            preparedStatement.setInt(5, product.quantityAvailable());
            preparedStatement.setString(6, product.imageUrlOne());
            preparedStatement.setString(7, product.imageUrlTwo());
            preparedStatement.setBoolean(8, true);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                productId = resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return productId;
    }

    //get all products
    public JsonObject getAllProducts(int accountId, int itemsPerPage, int offset) {
        String dataQuery = """
                SELECT p.product_id, p.name, p.price_per_unit, u.unit, p.quantity_available, 
                       p.image_url_1, p.image_url_2, p.status
                FROM products p
                INNER JOIN units u ON u.unit_id = p.unit_id
                WHERE p.account_id = ?
                LIMIT ? OFFSET ?""";
        String countQuery = "SELECT COUNT(*) FROM products WHERE account_id = ?";
        var responseJson = Json.createObjectBuilder();
        var products = Json.createArrayBuilder();
        int totalItems = 0;
        try (Connection connection = ads.getConnection()) {
            // Step 1: Count total items
            try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
                countStmt.setInt(1, accountId);
                ResultSet countResult = countStmt.executeQuery();
                if (countResult.next()) {
                    totalItems = countResult.getInt(1);
                }
            }
            // Step 2: Fetch paginated data
            try (PreparedStatement dataStmt = connection.prepareStatement(dataQuery)) {
                dataStmt.setInt(1, accountId);
                dataStmt.setInt(2, itemsPerPage);
                dataStmt.setInt(3, offset);
                ResultSet resultSet = dataStmt.executeQuery();
                while (resultSet.next()) {
                    var product = Json.createObjectBuilder()
                            .add("productId", resultSet.getInt(1))
                            .add("name", resultSet.getString(2))
                            .add("pricePerUnit", resultSet.getDouble(3))
                            .add("unitOfMeasurement", resultSet.getString(4))
                            .add("quantityAvailable", resultSet.getInt(5))
                            .add("imageUrl1", resultSet.getString(6))
                            .add("imageUrl2", resultSet.getString(7))
                            .add("status", resultSet.getBoolean(8));
                    products.add(product);
                }
            }
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
            // Step 3: Return full response
            responseJson
                    .add("totalItems", totalItems)
                    .add("totalPages", totalPages)
                    .add("itemsPerPage", itemsPerPage)
                    .add("offset", offset)
                    .add("products", products);
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return responseJson.build();
    }
    //update product
//    public boolean updateProductDetails (){
//        String query=""
//    }


}
