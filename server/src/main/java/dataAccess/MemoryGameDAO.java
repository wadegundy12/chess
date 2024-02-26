package dataAccess;

import chess.ChessGame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{

    private static Map<String, ChessGame> games = new HashMap<>();

    @Override
    public boolean createGame(String gameName) {
        if (games.containsKey(gameName)){
            return false;
        }
        games.put(gameName, new ChessGame());
        return true;
    }

    @Override
    public ChessGame getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<ChessGame> listGames() {
        return null;
    }

    @Override
    public void updateGame(int gameID) {

    }
}
