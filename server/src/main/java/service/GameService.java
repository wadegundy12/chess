package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Map;

public class GameService {

    private AuthDAO aData = new MemoryAuthDAO();
    private UserDao uData = new MemoryUserDAO();

    private GameDAO gData = new MemoryGameDAO();

    public Collection<GameData> listGames(){
        return gData.listGames();
    }

    public int createGame(String gameName, String authToken) throws DataAccessException {
        boolean found = false;
        for (Map.Entry<String, AuthData> entry : aData.getAuthList().entrySet()) {
            AuthData tempData = entry.getValue();
            if(tempData.authToken().equals(authToken)){
                found = true;
                break;
            }
        }

        if(!found){
            throw new DataAccessException("Error: unauthorized");
        }

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
