package service;

import dataAccess.*;
import dataAccess.memoryDAOs.MemoryAuthDAO;
import dataAccess.memoryDAOs.MemoryGameDAO;
import dataAccess.memoryDAOs.MemoryUserDAO;
import dataAccess.parentDAOs.AuthDAO;
import dataAccess.parentDAOs.GameDAO;
import dataAccess.parentDAOs.UserDao;
import model.GameData;

import java.util.Collection;

public class GameService {

    private AuthDAO aData = new MemoryAuthDAO();
    private UserDao uData = new MemoryUserDAO();

    private GameDAO gData = new MemoryGameDAO();

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        if(!aData.getAuthList().containsKey(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }
        return gData.listGames();
    }

    public int createGame(String gameName, String authToken) throws DataAccessException {
        if(!aData.getAuthList().containsKey(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }

            return gData.createGame(gameName);
    }

    public void joinGame(String teamColor, int gameID, String authToken) throws DataAccessException {

        if(!aData.getAuthList().containsKey(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }

        String username = aData.getAuthList().get(authToken).username();

        GameData tempData = gData.getGame(gameID);


        if (teamColor != null) {
            if (teamColor.equalsIgnoreCase("BLACK")){
                if(tempData.getBlackUsername() != null){
                    throw new DataAccessException("Error: already taken");
                }
                tempData.setBlackUsername(username);
            }
            else  if (teamColor.equalsIgnoreCase("WHITE")){
                if(tempData.getWhiteUsername() != null){
                    throw new DataAccessException("Error: already taken");
                }
                tempData.setWhiteUsername(username);
            }
        }



        gData.updateGame(gameID, tempData);

    }

    //for testing

    public void clear(){
        uData.clear();
        aData.clear();
        gData.clear();
    }

    public GameData getGameData(int gameID) throws DataAccessException {
        return gData.getGame(gameID);
    }
}
