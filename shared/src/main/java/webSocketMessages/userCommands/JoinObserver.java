package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand{
    public int gameID;

    public JoinObserver(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        commandType = CommandType.JOIN_OBSERVER;

    }
}
