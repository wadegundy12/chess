import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.UserData;
import server.handlers.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ChessClient {

    private GameHandler gameHandler;
    private LoginOutHandler loginHandler;
    private RegisterHandler registerHandler;
    private boolean loggedIn;
    Gson serializer = new Gson();

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
            case "register" -> register(params);
            default -> " ";
        };
    }

    private String register(String[] params) {
        if (params.length < 3) {
            return "Not enough information";
        }
        try {
            URL url = new URL("http://localhost:8080/user");
            String username = params[0];
            String password = params[1];
            String email = params[2];

            UserData user = new UserData(username, password, email);
            String jsonUser = serializer.toJson(user);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonUser.getBytes());
            outputStream.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
            return response.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
