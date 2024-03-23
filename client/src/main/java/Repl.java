import java.util.Scanner;

import ui.EscapeSequences;
import ui.EscapeSequences.*;

public class Repl {

    private final ChessClient client;

    public Repl() {
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
}
