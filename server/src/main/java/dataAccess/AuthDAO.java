package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public interface AuthDAO {

    String createAuth(String username);
    AuthData getAuth(String username) throws DataAccessException;
    void deleteAuth(String username) throws DataAccessException;
    void clear();

    Map<String, AuthData> getAuthList();
}
