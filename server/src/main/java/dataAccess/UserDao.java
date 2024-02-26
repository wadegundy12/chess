package dataAccess;

public interface UserDao {
    void clear();
    void createUser(String username, String password);
    User getUser(String username);


}
