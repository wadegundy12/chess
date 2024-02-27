package dataAccess;

import model.AuthData;

public interface AuthDAO {

    String createAuth(String username);
    AuthData getAuth(String username) throws DataAccessException;
    void deleteAuth(String username);
    void clear();
}
