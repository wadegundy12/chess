package dataAccess;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;


public class MemoryUserDAO implements UserDao{

    private static Collection<UserData> users = new HashSet<>();


    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(String username, String password, String email) {
        users.add(new UserData(username,password,email));
    }

    @Override
    public UserData getUser(String username) {
        for (UserData nextUser : users) {
            if (nextUser.username().equals(username)) {
                return nextUser;
            }
        }
        return null;

    }
}
