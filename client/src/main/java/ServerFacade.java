import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.handlers.records.GameName;
import server.handlers.records.JoinGameRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;
    private GameData[] games;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(UserData user) throws DataAccessException {
        String path = "/user";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public AuthData login(UserData user) throws DataAccessException {
        String path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public void logout(String authToken) throws DataAccessException{
        String path = "/session";
        this.makeRequest("DELETE", path, null, void.class, authToken);
    }

    public GameData[] listGames(String authToken) throws DataAccessException{
        String path = "/game";
        Collection<GameData> gameCollection = this.makeRequest("GET", path, null, Collection.class, authToken);
        games = gameCollection.toArray(new GameData[10]);
        return games;
    }

    public void createGame(GameName gameName, String authToken) throws DataAccessException {
        String path = "/game";
        this.makeRequest("POST", path, gameName, String.class, authToken);
    }

    public void joinGame(int gameNum, String teamColor) throws DataAccessException {
        String path = "/game";
        this.makeRequest("PUT", path, new JoinGameRequest(teamColor, games[gameNum].getGameID()), void.class,null);
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            if (authToken != null){
                writeHeader(request,http,authToken);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
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
        if (request != null) {
            http.setRequestProperty("Content-Type", "application/json");
        }
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
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException("failure: " + status);
        }
    }
}
