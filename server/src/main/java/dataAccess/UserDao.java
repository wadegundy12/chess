package dataAccess;

import model.UserData;

public interface UserDao {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);


}
