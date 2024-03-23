package serviceTests;


import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UserServiceTests {

    @BeforeEach
    public void clearDAOs(){
        UserService tempUserService = new UserService();
        tempUserService.clear();
    }

    @Test
    public void createValidUsers() throws DataAccessException {
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();

        testUsers.add(new UserData("Wade", "GUNDY", "wdg23@gmail",null));
        testUsers.add(new UserData("Nathan", "smelly", "IlvCS@yahoo", null));


        userService.register(new UserData("Wade", "GUNDY", "wdg23@gmail",null));
        userService.register(new UserData("Nathan", "smelly", "IlvCS@yahoo",null));
        Assertions.assertEquals(testUsers, userService.getUserList());

    }

    @Test
    public void noUsersAdded(){
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();
        Assertions.assertEquals(testUsers, userService.getUserList());
    }

    @Test
    public void testLogout() throws DataAccessException {
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();
        Map<String, AuthData> testAuths = new HashMap<>();

        UserData tempUser = new UserData("Wade", "GUNDY", "wdg23@gmail",null);
        testUsers.add(tempUser);
        AuthData authData = userService.register(tempUser);
        userService.logout(authData.authToken());
        Assertions.assertEquals(testUsers, userService.getUserList());
        Assertions.assertEquals(testAuths, userService.getAuthList());
    }


    @Test
    public void testLogin() throws DataAccessException {
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();
        Map<String, AuthData> testAuths = new HashMap<>();
        UserData tempUser = new UserData("Wade", "GUNDY", "wdg23@gmail",null);


        testUsers.add(tempUser);
        AuthData authData = userService.register(tempUser);
        userService.logout(authData.authToken());
        userService.login(tempUser);
        Assertions.assertEquals(testUsers, userService.getUserList());

        //checks size of auth list due to randomness of authToken
        Assertions.assertEquals(1, userService.getAuthList().size());

    }




}
