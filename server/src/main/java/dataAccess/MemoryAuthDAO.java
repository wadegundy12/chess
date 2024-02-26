package dataAccess;

import model.AuthData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO{

    private static Map<String, AuthData> auths = new HashMap<>();
    @Override
    public void createAuth(String username) {
        String authToken = UUID.randomUUID().toString().substring(0, 6);

        auths.put(authToken, new AuthData(authToken, username));
    }

    @Override
    public String getAuth(String authToken) throws DataAccessException {
        if (auths.get(authToken) == null){
            throw new DataAccessException("Auth does not exist");
        }

        return auths.get(authToken).username();
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }
}
