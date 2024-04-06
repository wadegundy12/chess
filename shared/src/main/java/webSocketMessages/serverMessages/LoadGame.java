package webSocketMessages.serverMessages;

public class LoadGame extends ServerMessage{
    public boolean game;
    public LoadGame() {
        super(ServerMessageType.LOAD_GAME);
        game = true;
    }
}
