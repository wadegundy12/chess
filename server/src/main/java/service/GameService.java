package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

public class GameService {

    private AuthDAO aData = new MemoryAuthDAO();
    private UserDao uData = new MemoryUserDAO();

    private GameDAO gData = new MemoryGameDAO();

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
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

    public void joinGame(String teamColor, int gameID, String authToken) throws DataAccessException {
        String username = "";
        boolean found = false;
        for (Map.Entry<String, AuthData> entry : aData.getAuthList().entrySet()) {
            AuthData tempData = entry.getValue();
            if(tempData.authToken().equals(authToken)){
                found = true;
                username = tempData.username();
                break;
            }
        }

        if(!found){
            throw new DataAccessException("Error: unauthorized");
        }

        GameData tempData = gData.getGame(gameID);

        if (teamColor == null) {return;}

        if (teamColor.equals("BLACK")){
            tempData.setBlackUsername(username);
        }
        else  if (teamColor.equals("WHITE")){
            tempData.setBlackUsername(username);
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
