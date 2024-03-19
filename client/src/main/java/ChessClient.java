import com.google.gson.Gson;
import model.UserData;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {

    private boolean loggedIn;
    private int authToken;
    Gson serializer = new Gson();

    public ChessClient() {
        loggedIn = false;
        authToken = 0;
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

            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                Scanner scanner = new Scanner(connection.getInputStream());
                authToken = scanner.nextInt();
                return "Logged in as " + username;
            }

            else if(responseCode == 403){
                return "Username already taken";
            }
            return "Bad request";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
