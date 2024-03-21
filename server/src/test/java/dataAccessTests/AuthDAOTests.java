package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import dataAccess.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.utils.Assert;

import java.util.ArrayList;
import java.util.Collection;

public class AuthDAOTests {

    @BeforeEach
    public void clearTables(){
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    public void testClear(){
        SQLAuthDAO authDAO = new SQLAuthDAO();
        String tempAuth = authDAO.createAuth("Wade");
        authDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth(tempAuth);
        });

    }

    @Test
    public void testCreateAuth(){
        SQLAuthDAO authDAO = new SQLAuthDAO();
        String tempAuth = authDAO.createAuth("Wade");
        Assert.notNull(tempAuth);
    }

    @Test
    public void failCreateAuth() {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        String tempAuth = authDAO.createAuth("Wade");
        Assert.notNull(tempAuth);
        Assertions.assertNull(authDAO.createAuth(null));
    }

    @Test
    public void getAuth() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        String tempAuth = authDAO.createAuth("Wade");
        Assertions.assertEquals(new AuthData(tempAuth, "Wade", null), authDAO.getAuth(tempAuth));
    }

    @Test
    public void failedGetAuth() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        String tempAuth = authDAO.createAuth("Wade");

        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth("wrongAuthToken");
        });
    }

    @Test
    public void deleteAuth() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        String tempAuth = authDAO.createAuth("Wade");
        authDAO.deleteAuth(tempAuth);
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth(tempAuth);
        });
    }

    @Test
    public void failedDeleteAuth() {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("tempAuth");
        });
    }

    @Test
    public void getAuthList() {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.createAuth("Wade");
        authDAO.createAuth("Test2");
        Assertions.assertEquals(2, authDAO.getAuthList().size());
    }

    @Test
    public void emptyAuthList() {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertEquals(0, authDAO.getAuthList().size());
    }


}
