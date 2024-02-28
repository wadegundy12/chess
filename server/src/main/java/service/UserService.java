package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private AuthDAO aData = new MemoryAuthDAO();
    private UserDao uData = new MemoryUserDAO();

    public AuthData register(UserData user){
        uData.createUser(user);
        String authToken = aData.createAuth(user.username());
        return new AuthData(authToken, user.username());
    }

    public AuthData login(UserData user) throws DataAccessException {

        if(uData.getUser(user.username()) == null){
            throw new DataAccessException("Error: unauthorized");
        }

        else if(!(uData.getPassword(user.username()).equals(user.password()))){
            throw new DataAccessException("Error: unauthorized");
        }



        return new AuthData(aData.createAuth(user.username()), user.username());
    }

    public void logout (AuthData authData) throws DataAccessException {
        if(!aData.getAuth(authData.username()).authToken().equals(authData.authToken())){
            throw new DataAccessException("Error: unauthorized");
        }
        aData.deleteAuth(authData.username());
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
