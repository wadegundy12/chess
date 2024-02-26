package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;


public class MemoryGameDAO implements GameDAO{

    private static Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(String gameName) {
        for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
            if (entry.getValue().gameName().equals(gameName)) {
                return;
            }
        }



        int gameID = Integer.parseInt(UUID.randomUUID().toString().substring(0, 6), 16);


        games.put(gameID, new GameData(gameID, "", "", gameName, new ChessGame()));
    }

    @Override
    public ChessGame getGame(int gameID) {
        return games.get(gameID).game();
    }

    @Override
    public Collection<ChessGame> listGames() {
        Collection<ChessGame> chessGames = new ArrayList<>();
        for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
            chessGames.add(entry.getValue().game());
        }
        return chessGames;
    }

    @Override
    public void updateGame(int gameID, GameData newData) throws DataAccessException {
        if (games.get(gameID) == null){
            throw new DataAccessException("message");
        }
        games.put(gameID, newData);
    }
}
