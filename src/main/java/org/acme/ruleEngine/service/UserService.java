package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.acme.dao.UserDao;
import org.acme.models.User;
import org.acme.records.ServiceResponse;

@ApplicationScoped
public class UserService {
    private final UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public ServiceResponse createSuperAdmin(User user){
        boolean isExists= userDao.isUserExists(user.username());
        if (!isExists){
            int userId = userDao.createUser(user);
            int userDetailsId=userDao.addUserDetails(userId, user);
            int roleId = userDao.addUserRole(userId, "SUPER_ADMIN");
            return (userId>0 && userDetailsId>0 && roleId>0)
                    ? new ServiceResponse(Response.Status.OK.getStatusCode(), true, "user created successfully")
                    : new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create user");
        }
        else return new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "user already exists");
    }
    @Transactional
    public ServiceResponse createAdmin(User user){
        boolean isExists= userDao.isUserExists(user.username());
        if (!isExists){
            int userId = userDao.createUser(user);
            int userDetailsId=userDao.addUserDetails(userId, user);
            int roleId = userDao.addUserRole(userId, "ADMIN");
            return (userId>0 && userDetailsId>0 && roleId>0)
                    ? new ServiceResponse(Response.Status.OK.getStatusCode(), true, "user created successfully")
                    : new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create user");
        }
        else return new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "user already exists");
    }
    @Transactional
    public ServiceResponse createClient(User user){
        boolean isExists= userDao.isUserExists(user.username());
        if (!isExists){
            int userId = userDao.createUser(user);
            int userDetailsId=userDao.addUserDetails(userId, user);
            int roleId = userDao.addUserRole(userId, "CLIENT");
            return (userId>0 && userDetailsId>0 && roleId>0)
                    ? new ServiceResponse(Response.Status.OK.getStatusCode(), true, "user created successfully")
                    : new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create user");
        }
        else return new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "user already exists");
    }

}
