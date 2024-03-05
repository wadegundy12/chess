package dataAccess;

import dataAccess.DataAccessException;
import model.UserData;
import dataAccess.DatabaseManager;
import java.sql.SQLException;
import java.util.Collection;

public interface UserDao {

    final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              username VARCHAR(50) NOT NULL,
              password VARCHAR(255) NOT NULL,
              email VARCHAR(100) NOT NULL,
              authtoken VARCHAR(255) PRIMARY KEY
              INDEX(authtoken)
            )
            """
    };
    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    Collection<UserData> getUserList();
    public String getPassword(String username);



}
