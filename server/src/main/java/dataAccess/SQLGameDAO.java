package dataAccess;

import model.GameData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {


    final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              gameID VARCHAR(255) PRIMARY KEY,
              gameData VARCHAR(500) NOT NULL
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
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData newData) throws DataAccessException {

    }

    @Override
    public void clear() {

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
