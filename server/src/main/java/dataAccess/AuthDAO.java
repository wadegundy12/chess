package dataAccess;

public interface AuthDAO {

    void createAuth(String username);
    String getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken);
}
