package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataAccess.*;
import dataAccess.memoryDAOs.MemoryAuthDAO;
import dataAccess.memoryDAOs.MemoryGameDAO;
import dataAccess.memoryDAOs.MemoryUserDAO;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDao;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService {

    private AuthDAO aData = new SQLAuthDAO();
    private UserDao uData = new SQLUserDAO();

    private GameDAO gData = new SQLGameDAO();

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
                if(tempData.getBlackUsername() != null && !Objects.equals(tempData.getBlackUsername(), username)){
                    throw new DataAccessException("Error: already taken");
                }
                tempData.setBlackUsername(username);
            }
            else  if (teamColor.equalsIgnoreCase("WHITE")){
                if(tempData.getWhiteUsername() != null && !Objects.equals(tempData.getWhiteUsername(), username)){
                    throw new DataAccessException("Error: already taken");
                }
                tempData.setWhiteUsername(username);
            }
        }



        gData.updateGame(gameID, tempData);

    }

    public void makeMove(ChessMove chessMove, int gameID, String authToken) throws DataAccessException, InvalidMoveException {
        if(!aData.getAuthList().containsKey(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }

        GameData tempData = gData.getGame(gameID);
        ChessGame tempGame = tempData.getGame();
        Collection<ChessMove> validMoves = tempGame.validMoves(chessMove.getStartPosition());
        if (validMoves.contains(chessMove)){
            tempGame.makeMove(chessMove);
        }

        tempData.setGame(tempGame);
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
