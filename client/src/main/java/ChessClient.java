import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import records.*;
import serverFacade.ServerFacade;
import ui.EscapeSequences;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.*;

public class ChessClient {

    private boolean loggedIn;
    private boolean inGame;
    private String authToken;
    String serverUrl = "http://localhost:8080";
    private ServerFacade server = new ServerFacade(serverUrl);
    private NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private GameData currentGameData;
    private ArrayList<GameData> games;
    public boolean joinedBlack;

    public ChessClient(NotificationHandler notificationHandler) {
        loggedIn = false;
        inGame = false;
        authToken = "0";
        games = new ArrayList<>();
        currentGameData = null;
        joinedBlack = false;
        this.notificationHandler = notificationHandler;
        ws = new WebSocketFacade(serverUrl, notificationHandler);


    }

    public String eval(String input){
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (!loggedIn) {
            return switch (cmd) {
                case "help" -> loggedOutHelp();
                case "quit" -> "Goodbye";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                default -> "Invalid Input" + loggedOutHelp();
            };
        }

        else if (!inGame) {
            return switch (cmd){
                case "help" -> loggedInHelp();
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "Goodbye";
                default -> "Invalid Input" + loggedInHelp();
            };
        }
        else{
            return switch (cmd){
                case "help" -> inGameHelp();
                case "redraw" -> drawBoard(joinedBlack);
                case "highlight" -> highlightBoard(params[0]);
                case "leave" -> leaveGame();
                case "make" -> makeMove(params);
                default -> "Invalid Input" + inGameHelp();
            };
        }
    }

    public void updateGame(GameData newData){
        currentGameData.setGame(newData.getGame());
    }

    private String makeMove(String[] params) {
        if (!Objects.equals(params[0], "move")){
            return "Invalid Input" + inGameHelp();
        }
        try {
            ChessPosition startPosition = positionConvert(params[1]);
            ChessPosition endPosition = positionConvert(params[2]);
            ChessMove move = new ChessMove(startPosition,endPosition,null);
            ChessGame tempGame = currentGameData.getGame();

            //if pawn and in position to possibly promote - promote
            if (tempGame.getBoard().getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN)
                    && (endPosition.getRow() == 1 || endPosition.getRow() == 8)) {
                move = pawnPromote(startPosition,endPosition);
            }

            ws.makeMove(currentGameData.getGameID(), authToken, move);
        } catch (DataAccessException e) {
            return "Invalid position";
        }

        return "";

    }

    private ChessMove pawnPromote(ChessPosition startPosition, ChessPosition endPosition){
        System.out.println("Enter Q N B or R to choose promotion piece: ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine().toUpperCase();
        System.out.println("\n");
        boolean goodInput;
        ChessMove move = new ChessMove(startPosition,endPosition,null);
        do {
            switch (line){
                case "Q" -> {
                    move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN);
                    goodInput = true;
                }
                case "N" -> {
                    move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT);
                    goodInput = true;
                }
                case "C" -> {
                    move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK);
                    goodInput = true;
                }
                case "B" ->{
                    move = new ChessMove(startPosition,endPosition, ChessPiece.PieceType.BISHOP);
                    goodInput = true;
                }
                default -> {
                    System.out.println("Enter Q N B or R to choose promotion piece: ");
                    line = scanner.nextLine().toUpperCase();
                    System.out.println("\n");
                    goodInput = false;
                }
            }
        } while(!goodInput);

        return move;
    }

    private String leaveGame() {
        ws.leave(currentGameData.getGameID(), authToken);
        inGame = false;
        currentGameData = null;
        joinedBlack = false;
        return "Left the game";
    }


    private String inGameHelp() {
        return """
                \tmake move <start position> <end position>
                \tresign - forfeit the game
                \tleave - leave the game
                \thighlight <start position> - show available moves
                \tredraw - the board
                \thelp - with possible commands
                """;
    }

    private String loggedInHelp(){
        return """
                \tcreate <NAME> - a game
                \tlist - games
                \tjoin <ID> [WHITE|BLACK|<empty>] - a game
                \tobserve <ID> - a game
                \tlogout - when you are done
                \tquit - playing chess
                \thelp - with possible commands
                """;
    }

    private String loggedOutHelp(){
        return """
                \tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account
                \tlogin <USERNAME> <PASSWORD> - to play chess
                \tquit - playing chess
                \thelp - with possible commands
                """;
    }

    private String register(String[] params){
        if(params.length < 3){
            return "Not enough information given";
        }
        UserData user = new UserData(params[0], params[1], params[2], null);

        AuthData authData = server.register(user);
        if (authData.errorMessage() != null) {
            return authData.errorMessage();
        }
        authToken = authData.authToken();
        loggedIn = true;
        return "Successfully registered and logged in";

    }

    private String login(String[] params){
        if (loggedIn){
            return "Already logged in";
        }
        if(params.length < 2){
            return "Not enough information given";
        }
        UserData user = new UserData(params[0],params[1],null,null);
        AuthData authData = server.login(user);
        if (authData.errorMessage() != null) {
            return authData.errorMessage();
        }
        authToken = authData.authToken();
        loggedIn = true;
        return "Successfully logged in";
    }

    private String logout(){
        String result = server.logout(authToken).message();
        if(result != null){
            return result;
        }
        loggedIn = false;
        return "Successfully logged out";
    }

    private String createGame(String[] params){
        if (!loggedIn){
            return "Not logged in";
        }
        String result = server.createGame(new GameName(params[0]), authToken).errorMessage();
        if(result != null){
            return result;
        }
        return "Game created";
    }

    private String listGames(){
        StringBuilder output = new StringBuilder("Games:\n");
        if (!loggedIn){
            return "Not logged in";
        }
        GamesListRecord gamesListRecord = server.listGames(authToken);
        if (gamesListRecord.errorMessage() != null){
            return gamesListRecord.errorMessage();
        }
        games.clear();
        games.addAll(gamesListRecord.games());
        if (games.isEmpty()){
            return "No current games to list";
        }
        for (int i = 0; i < games.size(); i++){
            output.append("\tGame ").append(i + 1);
            output.append(": Name: ").append(games.get(i).getGameName());
            output.append("\n\t\tWhite Username: ").append(games.get(i).getWhiteUsername());
            output.append("\n\t\tBlack Username: ").append(games.get(i).getBlackUsername()).append("\n");
        }
        return output.toString();
    }

    private String joinGame(String[] params) {
        int gameNum;
        String teamColor;
        try{
            if(params.length < 1){
                return "Not enough information";
            }
            teamColor = (params.length >= 2) ?
                    params[1].substring(0,1).toUpperCase() + params[1].substring(1) : null;
            gameNum = Integer.parseInt(params[0]) - 1;

            if (gameNum < 0 | gameNum >= games.size()){
                return "Game number out of range";
            }
            if(teamColor == null){
                teamColor = "observer";
            }

            ErrorData errorMessage = server.joinGame(games.get(gameNum).getGameID(),teamColor, authToken);
            if(errorMessage.message()!= null){

            }
            ws.joinGame(games.get(gameNum).getGameID(), teamColor, authToken);
            inGame = true;

        } catch (NumberFormatException e) {
            return "Did not input a valid number";
        }

        joinedBlack = (teamColor.equals("Black"));
        String output = "Joined game as " + teamColor + "\n" + teamColor + "'s View:\n\u001B[0m";
        //output += drawBoard((teamColor.equals("Black")));
        //output += drawBoard((!teamColor.equals("Black")));
        currentGameData = games.get(gameNum);
        return output;
    }

    private String observeGame(String[] params) {
        int gameNum;
        try {
            if (params.length < 1) {
                return "Not enough information";
            }
            gameNum = Integer.parseInt(params[0]) - 1;
            if (gameNum < 0 | gameNum >= games.size()) {
                return "Game number out of range";
            }
            String errorMessage = server.joinGame(games.get(gameNum).getGameID(), null, authToken).message();
            ws.joinObserver(games.get(gameNum).getGameID(), authToken);

            if (errorMessage != null){
                return errorMessage;
            }

        } catch (NumberFormatException e) {
            return "Did not input a valid number";
        }
        String output = "Joined game as observer";
        joinedBlack = false;
        currentGameData = games.get(gameNum);
        inGame = true;
        //output += drawBoard(false);
        //output += drawBoard(true);
        return output;
    }

    public String drawBoard(boolean blackPerspective){
        String resetStyle = "\u001B[0m";
        StringBuilder output = new StringBuilder();
        char[] columnLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        String background = EscapeSequences.SET_BG_COLOR_DARK_GREEN;

        for(int i = 0; i < 10; i++){
            output.append(background).append("   ");
        }
        output.append(resetStyle).append("\n");

        for (int row = blackPerspective ? 1:8; blackPerspective ? row <= 8 : row >= 1; row += blackPerspective ? 1 : -1) {

            output.append(background).append(" ").append(row).append(" ");

            for (int col = blackPerspective ? 8 : 1; blackPerspective ? col >= 1 : col <= 8; col += blackPerspective ? -1 : 1) {
                ChessPiece piece = currentGameData.getGame().getBoard().getPiece(new ChessPosition(row,col));
                boolean isWhiteSquare = (row + col) % 2 == 1;
                String squareBackground = isWhiteSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                output.append(squareBackground);

                if (piece == null){
                    output.append("   ");
                }
                else{
                    String pieceColorCode = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ?
                            EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
                    output.append(" ").append(pieceColorCode).append(piece.toCharacter()).append(" ");
                }

                output.append(resetStyle);

            }
            output.append(background).append("   ").append(resetStyle).append("\n");
        }
        output.append(background).append("   ");
        for (int i = blackPerspective ? 8 : 1; blackPerspective ? i >= 1 : i <= 8; i += blackPerspective ? -1 : 1) {
            output.append(background).append(" ").append(columnLetters[i - 1]).append(" ");
        }
        output.append(background).append("   ").append(resetStyle).append("\n");
        output.append("\n");
        return output.toString();
    }

    public String highlightBoard(String stringPosition){
        ChessPosition position;
        try {
            position = positionConvert(stringPosition);
        } catch (DataAccessException e) {
            return "Error: Invalid Position";
        }
        Collection<ChessMove> moves = currentGameData.getGame().validMoves(position);
        ArrayList<ChessPosition> finalPositions = new ArrayList<>();
        for (ChessMove tempMove : moves){
            finalPositions.add(tempMove.getEndPosition());
        }
        String resetStyle = "\u001B[0m";
        StringBuilder output = new StringBuilder();
        char[] columnLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        String boarderColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        String highlighted = EscapeSequences.SET_BG_COLOR_YELLOW;

        for(int i = 0; i < 10; i++){
            output.append(boarderColor).append("   ");
        }
        output.append(resetStyle).append("\n");

        for (int row = joinedBlack ? 1:8; joinedBlack ? row <= 8 : row >= 1; row += joinedBlack ? 1 : -1) {

            output.append(boarderColor).append(" ").append(row).append(" ");

            for (int col = joinedBlack ? 8 : 1; joinedBlack ? col >= 1 : col <= 8; col += joinedBlack ? -1 : 1) {
                ChessPiece piece = currentGameData.getGame().getBoard().getPiece(new ChessPosition(row,col));
                boolean isWhiteSquare = (row + col) % 2 == 1;
                String squareBackground;
                if(finalPositions.contains(new ChessPosition(row, col))){
                    squareBackground = highlighted;
                }
                else {
                    squareBackground = isWhiteSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                }
                output.append(squareBackground);

                if (piece == null){
                    output.append("   ");
                }
                else{
                    String pieceColorCode = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ?
                            EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
                    output.append(" ").append(pieceColorCode).append(piece.toCharacter()).append(" ");
                }

                output.append(resetStyle);

            }
            output.append(boarderColor).append("   ").append(resetStyle).append("\n");
        }
        output.append(boarderColor).append("   ");
        for (int i = joinedBlack ? 8 : 1; joinedBlack ? i >= 1 : i <= 8; i += joinedBlack ? -1 : 1) {
            output.append(boarderColor).append(" ").append(columnLetters[i - 1]).append(" ");
        }
        output.append(boarderColor).append("   ").append(resetStyle).append("\n");
        output.append("\n");
        return output.toString();
    }

    private ChessPosition positionConvert(String position) throws DataAccessException {
        if (position.length() != 2){
            throw new DataAccessException("Not a valid input");
        }
        char columnChar = position.charAt(0);
        int column = columnChar - 'a' + 1;

        char rowChar = position.charAt(1);
        int row = Character.getNumericValue(rowChar);

        return new ChessPosition(row,column);
    }

}