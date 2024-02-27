package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;


public class MemoryGameDAO implements GameDAO{

    private static Map<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(String gameName) throws DataAccessException {
        for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
            if (entry.getValue().getGameName().equals(gameName)) {
                throw new DataAccessException("Game already exists");
            }
        }



        int gameID = Integer.parseInt(UUID.randomUUID().toString().substring(0, 6), 16);


        games.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));

        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> chessGames = new ArrayList<>();
        for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
            chessGames.add(entry.getValue());
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

    public void clear(){
        games.clear();
    }
}
