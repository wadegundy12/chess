package dataAccess;

import model.AuthData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO{

    private static Map<String, AuthData> auths = new HashMap<>();
    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString().substring(0, 6);

        auths.put(username, new AuthData(authToken, username));

        return authToken;
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        if (auths.get(username) == null){
            throw new DataAccessException("Auth does not exist");
        }

        return auths.get(username);
    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {
        if(!auths.containsKey(username)){
            throw new DataAccessException("Error: unauthorized");
        }
        auths.remove(username);
    }

    public void clear(){
        auths.clear();
    }

    @Override
    public Map<String, AuthData> getAuthList() {
        return auths;
    }


}
