import server.handlers.*;

import java.util.Arrays;

public class ChessClient {

    private GameHandler gameHandler;
    private LoginOutHandler loginHandler;
    private RegisterHandler registerHandler;
    private boolean loggedIn;

    public ChessClient() {
        gameHandler = new GameHandler();
        loginHandler = new LoginOutHandler();
        registerHandler = new RegisterHandler();
        loggedIn = false;
    }

    public String eval(String input){
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            default -> " ";
        };
    }

    private String register(String[] params){
        if(params.length == 3){

        }
    }
}
