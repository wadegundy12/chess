package dataAccess;

import chess.ChessGame;
import model.GameData;


import java.util.Collection;

public interface GameDAO {
    void createGame(String gameName);
    ChessGame getGame(int gameID);
    Collection<ChessGame> listGames();
    void updateGame(int gameID, GameData newData) throws DataAccessException;
}
