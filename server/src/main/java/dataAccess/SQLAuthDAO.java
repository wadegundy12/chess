package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.*;
import java.util.*;

public class SQLAuthDAO implements AuthDAO {

    final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              authToken VARCHAR(255) PRIMARY KEY,
              username VARCHAR(50) NOT NULL
            )
            """
    };


    public SQLAuthDAO(){

        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString().substring(0, 6);
        try(Connection connection = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, authToken);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try(Connection connection = DatabaseManager.getConnection()){
            String sql = "SELECT * FROM auths WHERE authToken = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, authToken);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String foundAuthToken = rs.getString("authToken");
                        String username = rs.getString("password");
                        return new AuthData(foundAuthToken, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try(Connection connection = DatabaseManager.getConnection()){
            String sql = "DELETE FROM auths WHERE authToken = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, authToken);

            }
        } catch (SQLException e) {
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
    public Map<String, AuthData> getAuthList() {
        try(Connection connection = DatabaseManager.getConnection()){
            Map<String, AuthData> authList = new HashMap<>();
            String sql = "SELECT * FROM auths";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String authToken = rs.getString("authToken");
                    String username = rs.getString("username");
                    authList.put(authToken, new AuthData(authToken, username));
                }
            }
            return authList;
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
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
}
