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
    public void createUser(UserData user) {
        users.add(user);
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

    public String getPassword(String username){
        for (UserData nextUser : users) {
            if (nextUser.username().equals(username)) {
                return nextUser.password();
            }
        }
        return null;
    }

    @Override
    public Collection<UserData> getUserList() {
        return users;
    }


}
