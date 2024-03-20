import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.UserData;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {

    private boolean loggedIn;
    private String authToken;
    private ServerFacade server = new ServerFacade("http://localhost:8080");
    Gson serializer = new Gson();

    public ChessClient() {
        loggedIn = false;
        authToken = "0";

    }

    public String eval(String input){
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "help" -> (loggedIn) ? loggedInHelp() : loggedOutHelp();
            case "register" -> register(params);
            case "login" -> login(params);
            default -> " ";
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



    public boolean isLoggedIn(){
        return loggedIn;
    }
}
