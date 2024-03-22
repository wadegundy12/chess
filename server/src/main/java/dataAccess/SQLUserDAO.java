package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SQLUserDAO implements UserDao {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              username VARCHAR(50) PRIMARY KEY,
              password VARCHAR(255) NOT NULL,
              email VARCHAR(100) NOT NULL
            )
            """
    };


    public SQLUserDAO(){
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try(Connection connection = DatabaseManager.getConnection()){
            String sql = "DELETE FROM users";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try(Connection connection = DatabaseManager.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            if (isUsernameTaken(connection, user.username())){
                throw new DataAccessException("Error: already taken");
            }
            if(user.password() == null){
                throw new DataAccessException("Error: bad request");

            }



            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.username());
                statement.setString(2, encodePassword(user.password()));
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserData getUser(String username) {
        try(Connection connection = DatabaseManager.getConnection()){
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String foundUsername = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(foundUsername, password, email, null);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e){
            return null;
        }
        return null;
    }

    @Override
    public Collection<UserData> getUserList() {
        try(Connection connection = DatabaseManager.getConnection()){
            Collection<UserData> userList = new ArrayList<>();
            String sql = "SELECT * FROM users";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    userList.add(new UserData(username, password, email, null));
                }
            }
            return userList;
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkPassword(String username, String passwordToCheck){
        UserData user = getUser(username);
        if (user == null){return false;}
        return passwordMatcher(passwordToCheck, user.password());
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private boolean isUsernameTaken(Connection conn, String username) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String encodePassword(String password){
        return encoder.encode(password);
    }

    private boolean passwordMatcher(String password, String codedPassword){
        return encoder.matches(password, codedPassword);
    }


}
