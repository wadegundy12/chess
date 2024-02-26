package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

public class UserService {

    private AuthDAO aData = new MemoryAuthDAO();
    private UserDao uData = new MemoryUserDAO();

    public AuthData register(UserData user){
        uData.createUser(user);
        String authToken = aData.createAuth(user.username());
        return new AuthData(authToken, user.username());
    }

    public AuthData login(UserData user) throws DataAccessException {
        return aData.getAuth(user.username());
    }

    public void logout (UserData user){
        aData.deleteAuth(user.username());
    }
}
