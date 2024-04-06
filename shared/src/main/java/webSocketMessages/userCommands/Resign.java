package webSocketMessages.userCommands;

public class Resign extends UserGameCommand{
    public int gameID;
    public Resign(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        commandType = CommandType.RESIGN;

    }
}
