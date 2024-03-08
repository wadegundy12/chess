package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLGameDAO implements GameDAO {


    final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              gameID VARCHAR(255) PRIMARY KEY,
              gameData longtext NOT NULL
            )
            """
    };


    public SQLGameDAO(){
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            if (gameExists(connection, gameName)) {
                throw new DataAccessException("Game already exists");
            }

            int gameID = Integer.parseInt(UUID.randomUUID().toString().substring(0, 6), 16);
            String gameString = gameToString(new GameData(gameID, null, null, gameName, new ChessGame()));
            System.out.println(gameString);

            String sql = "INSERT INTO games (gameID, gameData) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, gameID);
                statement.setString(2, gameString);
                statement.executeUpdate();
            }
            return gameID;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT gameData FROM games WHERE gameID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, gameID);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return stringToGame(rs.getString("gameData"));
                    }
                }
            }
            throw new DataAccessException("Error: bad request");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public Collection<GameData> listGames() {
            try (Connection connection = DatabaseManager.getConnection()) {
                Collection<GameData> gameDataCollection = new ArrayList<>();
                String sql = "SELECT gameData FROM games";
                try (PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        String gameString = rs.getString("gameData");
                        gameDataCollection.add(stringToGame(gameString));
                    }
                }
                return gameDataCollection;
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        }

    @Override
    public void updateGame(int gameID, GameData newData) throws DataAccessException {
        getGame(gameID);
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "UPDATE games SET gameData = ? WHERE gameID = ?";
            String newGameString = gameToString(newData);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newGameString);
                statement.setInt(2, gameID);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public void clear() {
        try(Connection connection = DatabaseManager.getConnection()){
            String sql = "DELETE FROM games";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

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

    private String gameToString(GameData game){
        Gson gson = new Gson();
        return gson.toJson(game);
    }

    private GameData stringToGame(String game){
        Gson gson = new Gson();
        return gson.fromJson(game, GameData.class);
    }

    private boolean gameExists(Connection connection, String gameName) throws SQLException {
        String sql = "SELECT gameData FROM games";
        try (PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String gameString = rs.getString("gameData");
                GameData gameData = stringToGame(gameString);
                if (gameData.getGameName().equals(gameName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
