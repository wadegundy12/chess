import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.handlers.records.GameName;
import server.handlers.records.GamesListRecord;

import java.util.Arrays;

public class ChessClient {

    private boolean loggedIn;
    private String authToken;
    private ServerFacade server = new ServerFacade("http://localhost:8080");

    private GameData[] gameArray;

    public ChessClient() {
        loggedIn = false;
        authToken = "0";
        gameArray = null;

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
                default -> " ";
            };
        }
        return switch (cmd){
            case "help" -> loggedInHelp();
            case "logout" -> logout();
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> joinGame(params);
            case "observe" -> observeGame(params);

            default -> "";
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
        String result = server.logout(authToken);
        if(!result.isEmpty()){
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

        gameArray = gamesListRecord.games().toArray(new GameData[0]);
        if (gameArray.length == 0){
            return "No current games to list";
        }
        for (int i = 0; i < gameArray.length; i++){
            output.append("\tGame ").append(i);
            output.append(": \u001B[32Name: ").append(gameArray[i].getGameName()).append("\n\t\tWhite Username: ");
            output.append("\n\t\t\u001B[32White Username: ").append(gameArray[i].getWhiteUsername());
            output.append("\n\t\t\u001B[32Black Username: ").append(gameArray[i].getBlackUsername()).append("\u001B[0m\n");
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
            teamColor = (params.length >= 2) ? params[1] : null;
            gameNum = Integer.parseInt(params[0]);

            if (gameNum < 0 | gameNum >= gameArray.length){
                return "Game number out of range";
            }
            String errorMessage = server.joinGame(gameNum, teamColor).message();
            if (errorMessage != null){
                return errorMessage;
            }
        } catch (NumberFormatException e) {
            return "Did not input a valid number";
        }


        return "Joined game as" + teamColor;
    }

    private String observeGame(String[] params) {
        int gameNum;
        try {
            if (params.length < 1) {
                return "Not enough information";
            }
            gameNum = Integer.parseInt(params[0]);
            if (gameNum < 0 | gameNum >= gameArray.length) {
                return "Game number out of range";
            }
            String errorMessage = server.joinGame(gameNum, null).message();
            if (errorMessage != null){
                return errorMessage;
            }

        } catch (NumberFormatException e) {
            return "Did not input a valid number";
        }
        return "Joined game as observer";
    }

}
