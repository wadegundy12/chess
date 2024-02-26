package dataAccess;

public interface AuthDAO {

    void createAuth();
    String getAuth(String authToken);
    void deleteAuth(String authToken);
}
