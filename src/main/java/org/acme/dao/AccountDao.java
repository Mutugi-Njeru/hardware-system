package org.acme.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.models.Account;
import org.acme.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class AccountDao {
    private final AgroalDataSource ads;
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    @Inject
    public AccountDao(AgroalDataSource ads) {
        this.ads = ads;
    }

    //create account
    public boolean isAccountExist(String registrationPin) {
        String query = "SELECT 1 FROM account WHERE registration_pin = ? LIMIT 1";

        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, registrationPin);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
            return false;
        }
    }

    public int createAccount(Account account) {
        String query = "INSERT INTO account (business_name, registration_pin, kra_pin, is_active) VALUES (?, ?, ?, ?)";
        int accountId = 0;

        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, account.businessName());
            preparedStatement.setString(2, account.registrationPin()); 
            preparedStatement.setString(3, account.kraPin());
            preparedStatement.setBoolean(4, true);

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                accountId = resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }

        return accountId;
    }


}
