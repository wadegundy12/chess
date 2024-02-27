package dataAccess;

import model.GameData;


import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(int gameID, GameData newData) throws DataAccessException;
    void clear();
}
