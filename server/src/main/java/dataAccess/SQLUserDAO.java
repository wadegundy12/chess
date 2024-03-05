package dataAccess;

import dataAccess.DataAccessException;
import dataAccess.parentDAOs.UserDao;
import model.UserData;

import java.util.Collection;

public class SQLUserDAO implements UserDao {
    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<UserData> getUserList() {
        return null;
    }

    @Override
    public String getPassword(String username) {
        return null;
    }
}
