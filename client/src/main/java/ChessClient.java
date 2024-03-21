import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.GameData;
import model.UserData;
import server.handlers.records.GameName;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

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
                case "join" -> join(params);
                default -> " ";
            };
        }
        return switch (cmd){
            case "help" -> loggedInHelp();
            case "logout" -> logout();
            case "create" -> createGame(params);
            case "list" -> listGames();
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
        UserData user = new UserData(params[0], params[1], params[2]);
        try {
            authToken = server.register(user).authToken();
            loggedIn = true;
            return "Successfully registered and logged in";
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private String login(String[] params){
        if (loggedIn){
            return "Already logged in";
        }
        if(params.length < 2){
            return "Not enough information given";
        }
        UserData user = new UserData(params[0],params[1],null);
        try {
            authToken = server.login(user).authToken();
            loggedIn = true;
            return "Successfully logged in";
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private String logout(){
        if (!loggedIn){
            return "Already logged out";
        }
        try {
            server.logout(authToken);
            loggedIn = false;
            return "Successfully logged out";
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private String createGame(String[] params){
        if (!loggedIn){
            return "Not logged in";
        }
        try {
            server.createGame(new GameName(params[0]), authToken);
            return "Game created";
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String listGames(){
        StringBuilder output = new StringBuilder("Games:\n");
        if (!loggedIn){
            return "Not logged in";
        }
        try{
            gameArray = server.listGames(authToken);
            for (int i = 0; i < gameArray.length; i++){
                output.append("\tGame ").append(i);
                output.append(": \u001B[32Name: ").append(gameArray[i].getGameName()).append("\n\t\tWhite Username: ");
                output.append("\n\t\t\u001B[32White Username: ").append(gameArray[i].getWhiteUsername());
                output.append("\n\t\t\u001B[32Black Username: ").append(gameArray[i].getBlackUsername()).append("\u001B[0m\n");
            }
            return output.toString();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }




}
