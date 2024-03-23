import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.handlers.records.GameName;
import server.handlers.records.GamesListRecord;
import serverFacade.ServerFacade;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Arrays;

public class ChessClient {

    private boolean loggedIn;
    private String authToken;
    private ServerFacade server = new ServerFacade("http://localhost:8080");

    private ArrayList<GameData> games;

    public ChessClient() {
        loggedIn = false;
        authToken = "0";
        games = new ArrayList<>();

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
                default -> "Invalid Input" + loggedOutHelp();
            };
        }
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

    private String loggedInHelp(){
        return """
                \tcreate <NAME> - \u001B[32ma game\u001B[0m
                \tlist - \u001B[32mgames\u001B[0m
                \tjoin <ID> [WHITE|BLACK|<empty>] - \u001B[32ma game\u001B[0m
                \tobserve <ID> - \u001B[32ma game\u001B[0m
                \tlogout - \u001B[32mwhen you are done\u001B[0m
                \tquit - \u001B[32mplaying chess\u001B[0m
                \thelp - \u001B[32mwith possible commands\u001B[0m
                """;
    }

    private String loggedOutHelp(){
        return """
                \tregister <USERNAME> <PASSWORD> <EMAIL> - \u001B[32mto create an account\u001B[0m
                \tlogin <USERNAME> <PASSWORD> - \u001B[32mto play chess\u001B[0m
                \tquit - \u001B[32mplaying chess\u001B[0m
                \thelp - \u001B[32mwith possible commands\u001B[0m
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
        if (!loggedIn){
            return "Already logged out";
        }
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

        games.addAll(gamesListRecord.games());
        if (games.isEmpty()){
            return "No current games to list";
        }
        for (int i = 0; i < games.size(); i++){
            output.append("\tGame ").append(i + 1);
            output.append(": Name: ").append(games.get(i).getGameName());
            output.append("\n\t\tWhite Username: ").append(games.get(i).getWhiteUsername());
            output.append("\n\t\tBlack Username: ").append(games.get(i).getBlackUsername()).append("\u001B[0m\n");
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
            String errorMessage = server.joinGame(gameNum, teamColor, authToken).message();
            if (errorMessage != null){
                return errorMessage;
            }
        } catch (NumberFormatException e) {
            return "Did not input a valid number";
        }

        if(teamColor == null){
            teamColor = "observer";
        }

        String output = "Joined game as " + teamColor + "\n" + teamColor + "'s View:\n";
        output += drawBoard(games.get(gameNum).getGame(), (teamColor.equals("White")));
        output += drawBoard(games.get(gameNum).getGame(), (teamColor.equals("Black")));
        return output;
    }

    private String observeGame(String[] params) {
        int gameNum;
        try {
            if (params.length < 1) {
                return "Not enough information";
            }
            gameNum = Integer.parseInt(params[0]);
            if (gameNum < 0 | gameNum >= games.size()) {
                return "Game number out of range";
            }
            String errorMessage = server.joinGame(gameNum, null, authToken).message();
            if (errorMessage != null){
                return errorMessage;
            }

        } catch (NumberFormatException e) {
            return "Did not input a valid number";
        }
        return "Joined game as observer";
    }

    private String drawBoard(ChessGame game, boolean blackPerspective){
        String resetStyle = "\u001B[0m";
        StringBuilder output = new StringBuilder();
        for (int row = blackPerspective ? 8 : 1; blackPerspective ? row >= 1 : row <= 8; row += blackPerspective ? -1 : 1) {
            for (int col = blackPerspective ? 8 : 1; blackPerspective ? col >= 1 : col <= 8; col += blackPerspective ? -1 : 1) {
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row,col));
                boolean isWhiteSquare = (row + col) % 2 == 0;
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
            output.append("\n");
        }
        output.append("\n");
        return output.toString();
    }

}
