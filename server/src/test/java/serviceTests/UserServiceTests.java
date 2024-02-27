package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UserServiceTests {

    @BeforeAll
    public static void clearDAOs(){
        UserService tempUserService = new UserService();
        tempUserService.clear();
    }

    @Test
    public void createValidUsers() {
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();

        testUsers.add(new UserData("Wade", "GUNDY", "wdg23@gmail"));
        testUsers.add(new UserData("Nathan", "smelly", "IlvCS@yahoo"));


        userService.register(new UserData("Wade", "GUNDY", "wdg23@gmail"));
        userService.register(new UserData("Nathan", "smelly", "IlvCS@yahoo"));
        Assertions.assertEquals(testUsers, userService.getUserList());

    }

    @Test
    public void noUsersAdded(){
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();
        Assertions.assertEquals(testUsers, userService.getUserList());
    }

    @Test
    public void testLogout(){
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();
        Map<String, AuthData> testAuths = new HashMap<>();

        UserData tempUser = new UserData("Wade", "GUNDY", "wdg23@gmail");
        testUsers.add(tempUser);



        userService.register(tempUser);
        userService.logout(tempUser);
        Assertions.assertEquals(testUsers, userService.getUserList());
        Assertions.assertEquals(testAuths, userService.getAuthList());
    }


    @Test
    public void testLogin(){
        UserService userService = new UserService();
        Collection<UserData> testUsers = new HashSet<>();
        Map<String, AuthData> testAuths = new HashMap<>();

        UserData tempUser = new UserData("Wade", "GUNDY", "wdg23@gmail");
        testUsers.add(tempUser);



        userService.register(tempUser);
        Assertions.assertEquals(testUsers, userService.getUserList());
        //tests size of authList due to the randomness of the authTokens
        Assertions.assertEquals(1, userService.getAuthList().size());


    }




}
