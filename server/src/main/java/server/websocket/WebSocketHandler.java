package server.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
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
            case JOIN_PLAYER -> joinPlayer(userGameCommand.getAuthString(), session);
            case JOIN_OBSERVER -> joinObserver(userGameCommand.getAuthString(), session);
            case MAKE_MOVE ->
            case LEAVE ->
            case RESIGN ->
        }
    }

    private void joinObserver(String authToken, Session session) {

    }

    private void joinPlayer(String authToken, Session session) throws IOException {
        connections.add(authToken, session);
        String userName = getUserName(authToken);
        String message = String.format("%s joined the game", userName);
        Notification notification = new Notification(message);
        connections.broadcast(userName, notification);
    }


    private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public String getUserName(String authToken){
         return userService.getAuthList().get(authToken).username();
    }
}