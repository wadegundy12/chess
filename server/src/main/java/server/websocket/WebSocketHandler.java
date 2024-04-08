package server.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(ServerMessage.class, new ServerMessage.ServerMessageDeserializer()).create();
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> loadGame((LoadGame) serverMessage);
            case ERROR -> error();
            case NOTIFICATION -> notificationMessage((Notification) serverMessage);
        }
    }

    private void enter(String visitorName, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }

    private void notificationMessage(Notification notification){
        System.out.println(notification.message);
    }

    private void loadGame(LoadGame serverMessage){

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
}