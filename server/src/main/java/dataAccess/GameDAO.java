package dataAccess;

import chess.ChessGame;
import model.GameData;


import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID);
    Collection<ChessGame> listGames();
    void updateGame(int gameID, GameData newData) throws DataAccessException;
    void clear();
}
