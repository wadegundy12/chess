package dataAccess;

import chess.ChessGame;

import java.util.Collection;

public interface GameDAO {
    boolean createGame(String gameName);
    ChessGame getGame(int gameID);
    Collection<ChessGame> listGames();
    void updateGame(int gameID);
}
