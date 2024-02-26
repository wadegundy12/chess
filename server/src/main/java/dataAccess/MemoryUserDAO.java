package dataAccess;
import java.util.HashMap;
import java.util.Map;


public class MemoryUserDAO implements UserDao{

    private static Map<String, String> users = new HashMap<>();


    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(String username, String password) {
        users.put(username, password);
    }

    @Override
    public User getUser(String username) {
        if(users.containsKey(username)){
            return new User(username, users.get(username));
        }
        return null;
    }
}
