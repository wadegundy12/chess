package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    String serverUrl;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            serverUrl = url;
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }


    public void joinGame(int gameID, String teamColor, String authToken) {
        ChessGame.TeamColor tempColor;
        switch (teamColor.toLowerCase()){
            case "white" -> tempColor = ChessGame.TeamColor.WHITE;
            case "black" -> tempColor = ChessGame.TeamColor.BLACK;
            default -> tempColor = null;
        }
        try{
            JoinPlayer joinPlayerCommand = new JoinPlayer(authToken, gameID, tempColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(joinPlayerCommand));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void joinObserver(int gameID, String authToken){
        try{
            JoinObserver joinObserverCommand = new JoinObserver(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(joinObserverCommand));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void leave(int gameID, String authToken){
        try{
            Leave leaveCommand = new Leave(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeMove(int gameID, String authToken, ChessMove move){
        try{
            MakeMove makeMove = new MakeMove(authToken,move, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMove));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}