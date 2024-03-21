package service;

import dataAccess.*;
import dataAccess.memoryDAOs.MemoryAuthDAO;
import dataAccess.memoryDAOs.MemoryUserDAO;
import dataAccess.AuthDAO;
import dataAccess.UserDao;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.Map;

public class UserService {

    private AuthDAO aData = new SQLAuthDAO();
    private UserDao uData = new SQLUserDAO();

    public AuthData register(UserData user) throws DataAccessException {
        uData.createUser(user);
        String authToken = aData.createAuth(user.username());
        return new AuthData(authToken, user.username(), null);
    }

    public AuthData login(UserData user) throws DataAccessException {

        if(uData.getUser(user.username()) == null){
            throw new DataAccessException("Error: unauthorized");
        }

        else if(!(uData.checkPassword(user.username(), user.password()))){
            throw new DataAccessException("Error: unauthorized");
        }



        return new AuthData(aData.createAuth(user.username()), user.username(), null);
    }

    public void logout (String authToken) throws DataAccessException {
        String username = "";
        boolean found = false;
        if (!aData.getAuthList().containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }

        aData.deleteAuth(authToken);
    }

    public void clear() {
        uData.clear();
        aData.clear();
    }

    //Methods used for testing

    public Collection<UserData> getUserList(){
        return uData.getUserList();
    }

    public Map<String, AuthData> getAuthList(){
        return aData.getAuthList();
    }
}
