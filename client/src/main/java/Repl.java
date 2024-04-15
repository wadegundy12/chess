import java.util.Scanner;

import com.google.gson.Gson;
import ui.EscapeSequences;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;
import websocket.NotificationHandler;

public class Repl implements NotificationHandler {

    private final ChessClient client;

    public Repl(){
        this.client = new ChessClient(this);
    }

    public void run() {
        System.out.println(client.eval("help"));
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Goodbye")) {
            System.out.flush();
            printPrompt();
            String line = scanner.nextLine();
            result = client.eval(line);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
        }

        System.out.println();

    }

    private void printPrompt() {
        System.out.print("\n\u001B[0m>>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage serverMessage, String message) {
        switch (serverMessage.getServerMessageType()){
            case LOAD_GAME -> {
                LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                client.updateGame(loadGame.game);
                System.out.println("\n" + client.drawBoard(client.joinedBlack) + "\n");
            }
            case ERROR -> {
                Error error = new Gson().fromJson(message, Error.class);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + error.errorMessage + "\n");
                System.out.flush();
            }
            case NOTIFICATION -> {
                Notification notification = new Gson().fromJson(message, Notification.class);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + notification.message + "\n");
                System.out.flush();
            }
        }
    }

}
