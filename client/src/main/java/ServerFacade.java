import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.handlers.records.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;
    private GameData[] games;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(UserData user) {
        String path = "/user";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public AuthData login(UserData user) {
        String path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public ErrorData logout(String authToken){
        String path = "/session";
        return this.makeRequest("DELETE", path, null, ErrorData.class, authToken);
    }

    public GamesListRecord listGames(String authToken){
        String path = "/game";
        GamesListRecord gamesListRecord = this.makeRequest("GET", path, null, GamesListRecord.class, authToken);
        Collection<GameData> gameCollection = gamesListRecord.games();
        games = gameCollection.toArray(new GameData[10]);
        return gamesListRecord;
    }

    public GameIDRecord createGame(GameName gameName, String authToken) {
        String path = "/game";
        return this.makeRequest("POST", path, gameName, GameIDRecord.class, authToken);
    }

    public ErrorData joinGame(int gameNum, String teamColor) {
        String path = "/game";
        return this.makeRequest("PUT", path, new JoinGameRequest(teamColor, games[gameNum].getGameID()), ErrorData.class,null);
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);


            if (authToken != null){
                writeHeader(request,http,authToken);
            }
            writeBody(request, http);
            http.connect();
            //throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static void writeHeader(Object request, HttpURLConnection http, String header) {
        if (header != null && !header.isEmpty()) {
            http.setRequestProperty("authorization", header);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) { 
                    response = new Gson().fromJson(reader, responseClass);
                }
            }catch (IOException ex){

            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new RuntimeException("HTTP response code didn't work");
        }
    }
}