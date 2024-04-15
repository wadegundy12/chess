package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;

import java.io.IOException;



@WebSocket
public class WebSocketHandler {

    private GameService gameService = new GameService();
    private UserService userService = new UserService();
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(UserGameCommand.class, new UserGameCommand.UserGameCommandDeserializer()).create();
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer((JoinPlayer)userGameCommand, session);
            case JOIN_OBSERVER -> joinObserver((JoinObserver)userGameCommand, session);
            case MAKE_MOVE -> makeMove((MakeMove)userGameCommand);
            case LEAVE -> leave((Leave)userGameCommand);
            case RESIGN -> resign((Resign)userGameCommand);
        }
    }

    private void makeMove(MakeMove userGameCommand) throws IOException {
        try {
            GameData tempGameData = gameService.getGameData(userGameCommand.gameID);
            String userName = getUserName(userGameCommand.getAuthString());
            boolean whiteTeam = userName.equals(tempGameData.getWhiteUsername());
            boolean blackTeam = userName.equals(tempGameData.getBlackUsername());
            ChessMove move = userGameCommand.move;
            ChessGame.TeamColor pieceColor = tempGameData.getGame().getBoard().getPiece(move.getStartPosition()).getTeamColor();
            if(!((whiteTeam && pieceColor.equals(ChessGame.TeamColor.WHITE)) || blackTeam && pieceColor.equals(ChessGame.TeamColor.BLACK))){
                throw new InvalidMoveException("Error: invalid move");
            }
            gameService.makeMove(userGameCommand.move, userGameCommand.gameID, userGameCommand.getAuthString());
            tempGameData = gameService.getGameData(userGameCommand.gameID);
            String message = String.format("%s moved from %s to %s", userName, move.getStartPosition(), move.getEndPosition());
            Notification notification = new Notification(message);
            connections.broadcast("", tempGameData.getGameID(), new LoadGame(tempGameData));
            connections.broadcast(userGameCommand.getAuthString(), tempGameData.getGameID(), notification);
        } catch (InvalidMoveException e) {
            connections.replyToRoot(userGameCommand.getAuthString(), new Error("Error: Invalid Move"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void resign(Resign userGameCommand) throws IOException {
        String userName = getUserName(userGameCommand.getAuthString());
        String message = String.format("%s resigned the game", userName);
        Notification notification = new Notification(message);


        try {
            boolean whiteTeam = userName.equals(gameService.getGameData(userGameCommand.gameID).getWhiteUsername());
            boolean blackTeam = userName.equals(gameService.getGameData(userGameCommand.gameID).getBlackUsername());
            if (!whiteTeam && !blackTeam){
                throw new DataAccessException("Error: Cannot resign as observer");
            }
            GameData gameData = gameService.getGameData(userGameCommand.gameID);
            if(gameData.isGameOver()){
                throw new DataAccessException("Error: Game already over");
            }

            gameService.endGame(userGameCommand.gameID);
            connections.broadcast("", gameData.getGameID(), notification);


        } catch (DataAccessException e) {
            connections.replyToRoot(userGameCommand.getAuthString(), new Error("Error: Invalid game"));
        }

    }

    private void leave(Leave userGameCommand) throws IOException {
        connections.remove(userGameCommand.getAuthString());
        String userName = getUserName(userGameCommand.getAuthString());
        String message = String.format("%s left the game", userName);
        Notification notification = new Notification(message);
        connections.broadcast(userGameCommand.getAuthString(), userGameCommand.gameID, notification);
    }

    private void joinObserver(JoinObserver joinObserver, Session session) throws IOException {

        try {
            connections.add(joinObserver.getAuthString(), joinObserver.gameID, session);
            gameService.joinGame(null,joinObserver.gameID, joinObserver.getAuthString());
            String userName = getUserName(joinObserver.getAuthString());
            String message = String.format("%s joined the game as an observer", userName);
            Notification notification = new Notification(message);
            connections.broadcast(joinObserver.getAuthString(), joinObserver.gameID, notification);
            connections.replyToRoot(joinObserver.getAuthString(), new LoadGame(gameService.getGameData(joinObserver.gameID)));
        } catch (DataAccessException e) {
            connections.replyToRoot(joinObserver.getAuthString(), new Error(e.getMessage()));

        }

    }

    private void joinPlayer(JoinPlayer joinPlayerObject, Session session) throws IOException {
        try {
            connections.add(joinPlayerObject.getAuthString(), joinPlayerObject.gameID, session);
            GameData tempData = gameService.getGameData(joinPlayerObject.gameID);
            teamColorEmpty(tempData, joinPlayerObject.playerColor);
            gameService.joinGame(joinPlayerObject.playerColor.toString(),joinPlayerObject.gameID, joinPlayerObject.getAuthString());
            String playerColor = String.valueOf(joinPlayerObject.playerColor).toLowerCase();
            String userName = getUserName(joinPlayerObject.getAuthString());
            String message = String.format("%s joined the game as %s", userName, playerColor);
            Notification notification = new Notification(message);
            connections.broadcast(joinPlayerObject.getAuthString(), joinPlayerObject.gameID, notification);
            LoadGame loadGame = new LoadGame(gameService.getGameData(joinPlayerObject.gameID));
            connections.replyToRoot(joinPlayerObject.getAuthString(), loadGame);

        } catch (DataAccessException e) {
            connections.replyToRoot(joinPlayerObject.getAuthString(), new Error(e.getMessage()));
        }

    }


    private String getUserName(String authToken){
         return userService.getAuthList().get(authToken).username();
    }

    private void teamColorEmpty(GameData gameData, ChessGame.TeamColor teamColor) throws DataAccessException{
        switch (teamColor){
            case WHITE -> {
                if (gameData.getWhiteUsername() == null){throw new DataAccessException("Somehow not logged in yet");}
            }
            case BLACK -> {
                if (gameData.getBlackUsername() == null){throw new DataAccessException("Somehow not logged in yet");}
            }
        }
    }


}