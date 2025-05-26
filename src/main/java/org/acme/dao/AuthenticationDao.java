package org.acme.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.records.Authentication;
import org.acme.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AuthenticationDao {
    private final AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(AuthenticationDao.class);

    @Inject
    public AuthenticationDao(AgroalDataSource ads) {
        this.ads = ads;
    }

    //authenticate user
    public Authentication authenticate(String username, String password) {
        String query = """
                SELECT r.role FROM roles r
                INNER JOIN users u ON u.user_id=r.user_id
                WHERE u.username=? AND u.password=? AND u.is_active=?""";
        List<String> roles = new ArrayList<>();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setBoolean(3, true);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    roles.add(resultSet.getString("role"));
                }
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return new Authentication(!roles.isEmpty(), username, roles);
    }
}
