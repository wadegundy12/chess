package dataAccess.parentDAOs;

import dataAccess.DataAccessException;
import model.AuthData;

import java.util.Map;

public interface AuthDAO {

    String createAuth(String username);
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear();

    Map<String, AuthData> getAuthList();
}
