public class Repl {

    private final ChessClient client;

    public Repl() {
        this.client = new ChessClient();
    }

    public void run() {
        System.out.println(client.eval("help"));
    }
}
