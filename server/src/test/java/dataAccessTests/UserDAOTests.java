package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SQLGameDAO;
import dataAccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class UserDAOTests {

    @BeforeEach
    public void clearTables(){
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void testClear() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        userDAO.clear();
        Collection<UserData> emptyList = new ArrayList<>();
        Assertions.assertEquals(userDAO.getUserList(), emptyList);

    }

    @Test
    public void testCreateUser() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        Assertions.assertEquals(userDAO.getUser("Wade").username(), "Wade");
        Assertions.assertEquals(userDAO.getUser("Wade").email(), "testEmail.com");
        Assertions.assertNotNull(userDAO.getUser("Wade").password());
    }

    @Test
    public void failCreateUser() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        });
    }

    @Test
    public void testGetUser() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        userDAO.createUser(new UserData("Test2","otherPass", "notAnEmail.com",null));

        Assertions.assertEquals(userDAO.getUser("Wade").email(), "testEmail.com");
        Assertions.assertNotNull(userDAO.getUser("Wade").password());
        Assertions.assertEquals(userDAO.getUser("Test2").username(), "Test2");
        Assertions.assertEquals(userDAO.getUser("Test2").email(), "notAnEmail.com");
        Assertions.assertNotNull(userDAO.getUser("Test2").password());
    }

    @Test
    public void failedGetUser() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        Assertions.assertNull(userDAO.getUser("Test2"));
    }

    @Test
    public void testGetList() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        userDAO.createUser(new UserData("Test2","otherPass", "notAnEmail.com",null));


        Assertions.assertEquals(2, userDAO.getUserList().size());
    }

    @Test
    public void testEmptyList() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        Assertions.assertEquals(0, userDAO.getUserList().size());
    }

    @Test
    public void testCheckPassword() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        Assertions.assertTrue(userDAO.checkPassword("Wade", "password"));
    }

    @Test
    public void wrongPassword() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.createUser(new UserData("Wade","password", "testEmail.com",null));
        Assertions.assertFalse(userDAO.checkPassword("Wade", "nope"));
    }





}
