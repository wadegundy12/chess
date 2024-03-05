package dataAccess;

import model.GameData;

import java.util.Collection;

public class SQLGameDAO implements GameDAO {
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
}
