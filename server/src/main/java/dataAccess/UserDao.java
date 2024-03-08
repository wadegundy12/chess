package dataAccess;

import dataAccess.DataAccessException;
import model.UserData;
import dataAccess.DatabaseManager;
import java.sql.SQLException;
import java.util.Collection;

public interface UserDao {


    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    Collection<UserData> getUserList();
    public boolean checkPassword(String username, String passwordToCheck);



}
