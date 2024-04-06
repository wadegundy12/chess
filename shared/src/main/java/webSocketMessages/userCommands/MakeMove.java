package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand{
    public int gameID;
    public ChessMove move;
    public MakeMove(String authToken, ChessMove move, int gameID) {
        super(authToken);
        this.move = move;
        this.gameID = gameID;
    }
}
