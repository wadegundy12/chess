package server.websocket;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccessException;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;


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
            case JOIN_OBSERVER -> joinObserver(userGameCommand.getAuthString(), session);
            case MAKE_MOVE -> makeMove((MakeMove)userGameCommand);
            case LEAVE -> leave((Leave)userGameCommand);
            case RESIGN -> resign((Resign)userGameCommand);
        }
    }

    private void makeMove(MakeMove userGameCommand) throws IOException {
        try {
            gameService.makeMove(userGameCommand.move, userGameCommand.gameID, userGameCommand.getAuthString());
            ChessMove move = userGameCommand.move;
            String userName = getUserName(userGameCommand.getAuthString());
            String message = String.format("%s moved from %s to %s", userName, move.getStartPosition(), move.getEndPosition());
            Notification notification = new Notification(message);
            connections.broadcast("", new LoadGame());
            connections.broadcast(userGameCommand.getAuthString(), notification);
        } catch (InvalidMoveException e) {
            connections.replyToRoot(userGameCommand.getAuthString(), new Error("Error: Invalid Move"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void resign(Resign userGameCommand) throws IOException {
        connections.remove(userGameCommand.getAuthString());
        String userName = getUserName(userGameCommand.getAuthString());
        String message = String.format("%s resigned the game, you win!", userName);
        Notification notification = new Notification(message);
        connections.broadcast(userGameCommand.getAuthString(), notification);
    }

    private void leave(Leave userGameCommand) throws IOException {
        connections.remove(userGameCommand.getAuthString());
        String userName = getUserName(userGameCommand.getAuthString());
        String message = String.format("%s left the game", userName);
        Notification notification = new Notification(message);
        connections.broadcast(userGameCommand.getAuthString(), notification);
    }

    private void joinObserver(String authToken, Session session) throws IOException {
        connections.add(authToken, session);
        String userName = getUserName(authToken);
        String message = String.format("%s joined the game as an observer", userName);
        Notification notification = new Notification(message);
        connections.broadcast(userName, notification);
        connections.replyToRoot(authToken, new LoadGame());

    }

    private void joinPlayer(JoinPlayer joinPlayerObject, Session session) throws IOException {
        connections.add(joinPlayerObject.getAuthString(), session);
        String userName = getUserName(joinPlayerObject.getAuthString());
        String playerColor = String.valueOf(joinPlayerObject.playerColor).toLowerCase();
        String message = String.format("%s joined the game as %s", userName, playerColor);
        Notification notification = new Notification(message);
        connections.broadcast(joinPlayerObject.getAuthString(), notification);
        connections.replyToRoot(joinPlayerObject.getAuthString(), new LoadGame());
    }


    private String getUserName(String authToken){
         return userService.getAuthList().get(authToken).username();
    }
}