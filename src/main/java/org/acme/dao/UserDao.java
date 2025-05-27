package org.acme.dao;

import io.agroal.api.AgroalDataSource;
import io.vertx.ext.auth.impl.hash.SHA1;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import org.acme.cipher.Sha256Hasher;
import org.acme.models.User;
import org.acme.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class UserDao {
    private final AgroalDataSource ads;
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final Sha256Hasher hasher;

    @Inject
    public UserDao(AgroalDataSource ads, Sha256Hasher hasher) {
        this.ads = ads;
        this.hasher = hasher;
    }
    //check if the user exists
    // create user
    // add roles
    // add user details

    public boolean isUserExists(String username) {
        String query = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return false;
    }

    // create user
    public int createUser(User user) {
        String query = """
                INSERT INTO users (account_id, username, password, is_active) VALUES (?, ?, ?, ?)""";
        String hashedPassword = hasher.createHashText(user.password());
        int userId = 0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, user.accountId());
            preparedStatement.setString(2, user.username());
            preparedStatement.setString(3, hashedPassword);
            preparedStatement.setBoolean(4, true);
            preparedStatement.executeUpdate();
            ResultSet resultSet=preparedStatement.getGeneratedKeys();

            while (resultSet.next()){
              userId=  resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return userId;
    }
    // add roles
    public int addUserRole(int userId, String role) {
        String query = "INSERT INTO roles (user_id, role) VALUES (?, ?)";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, role);
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
    //add user details
    public int addUserDetails(int userId, User user){
        String query="INSERT INTO user_details (user_id, firstname, lastname, msisdn, email) VALUES (?,?,?,?,?)";
        int userDetailsId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, user.firstname());
            preparedStatement.setString(3, user.lastname());
            preparedStatement.setString(4, user.msisdn());
            preparedStatement.setString(5, user.email());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                userDetailsId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return userDetailsId;
    }
    //get all users in an account
    //deactivate user
    // delete user

}
