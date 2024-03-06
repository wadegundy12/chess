package dataAccess;

import model.UserData;

import java.sql.*;
import java.util.Collection;

public class SQLUserDAO implements UserDao {

    final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              username VARCHAR(50) PRIMARY KEY,
              password VARCHAR(255) NOT NULL,
              email VARCHAR(100) NOT NULL
              INDEX(authtoken)
            )
            """
    };


    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
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
        try(Connection connection = DatabaseManager.getConnection()) {
            if (isUsernameTaken(connection, user.username())){
                throw new DataAccessException("Error: already taken");
            }
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.username());
                statement.setString(2, user.password());
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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

    public void configureDatabase() throws DataAccessException {
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


}
