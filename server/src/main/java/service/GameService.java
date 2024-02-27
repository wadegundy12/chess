package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {

    private AuthDAO aData = new MemoryAuthDAO();
    private UserDao uData = new MemoryUserDAO();

    private GameDAO gData = new MemoryGameDAO();

    public Collection<ChessGame> listGames(){
        return gData.listGames();
    }

    public int createGame(String gameName) throws DataAccessException {
        return gData.createGame(gameName);
    }

    public void joinGame(ChessGame.TeamColor teamColor, int gameID, String username) throws DataAccessException {
        GameData tempData = gData.getGame(gameID);
        AuthData authToken = aData.getAuth(username);

        if (teamColor == ChessGame.TeamColor.BLACK){
            tempData.setBlackUsername(authToken.username());
        }
        else{
            tempData.setWhiteUsername(authToken.username());
        }

        gData.updateGame(gameID, tempData);

    }

    //for testing

    public void clear(){
        uData.clear();
        aData.clear();
        gData.clear();
    }

    public GameData getGameData(int gameID){
        return gData.getGame(gameID);
    }
}
