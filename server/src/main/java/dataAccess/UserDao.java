package dataAccess;

import model.UserData;

import java.util.Collection;

public interface UserDao {
    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    Collection<UserData> getUserList();
    public String getPassword(String username);


}
