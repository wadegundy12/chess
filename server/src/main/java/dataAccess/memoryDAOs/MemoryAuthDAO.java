package dataAccess.memoryDAOs;

import dataAccess.parentDAOs.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {

    private static Map<String, AuthData> auths = new HashMap<>();
    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString().substring(0, 6);

        auths.put(authToken, new AuthData(authToken, username));

        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (auths.get(authToken) == null){
            throw new DataAccessException("Auth does not exist");
        }

        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authtoken) throws DataAccessException {
        if(!auths.containsKey(authtoken)){
            throw new DataAccessException("Error: unauthorized");
        }
        auths.remove(authtoken);
    }


    public void clear(){
        auths.clear();
    }

    @Override
    public Map<String, AuthData> getAuthList() {
        return auths;
    }


}
