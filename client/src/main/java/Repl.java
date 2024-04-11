import java.lang.Error;
import java.util.Scanner;

import ui.EscapeSequences;
import webSocketMessages.serverMessages.*;
import websocket.NotificationHandler;

public class Repl implements NotificationHandler {

    private final ChessClient client;

    public Repl(){
        this.client = new ChessClient();
    }

    public void run() {
        System.out.println(client.eval("help"));

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Goodbye")) {
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
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()){
            case LOAD_GAME -> {
                System.out.println(client.drawBoard(client.joinedBlack));
                printPrompt();
            }
            case ERROR -> {
                webSocketMessages.serverMessages.Error error = (webSocketMessages.serverMessages.Error) serverMessage;
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + error.errorMessage);
                printPrompt();
            }
            case NOTIFICATION -> {
                Notification notification = (Notification) serverMessage;
                System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + notification.message);
                printPrompt();
            }
        }
    }

}
