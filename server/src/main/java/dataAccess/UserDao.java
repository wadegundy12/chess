package dataAccess;

import model.UserData;

public interface UserDao {
    void clear();
    void createUser(String username, String password, String email);
    UserData getUser(String username);


}
