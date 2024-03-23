package serverFacade;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ServerFacade {

    private final String serverUrl;


    public ServerFacade(String url) {
        this.serverUrl = url;

    }

    public AuthData register(UserData user) {
        String path = "/user";
        try {
            return this.makeRequest("POST", path, user, AuthData.class, null);
        } catch (DataAccessException e) {
            return switch (e.getMessage()){
                case "400" -> new AuthData(null,null,"Error: bad request");
                case "403" -> new AuthData(null,null,"Error: already taken");
                default -> throw new IllegalStateException("Unexpected value: " + e.getMessage());
            };
        }
    }

    public AuthData login(UserData user) {
        String path = "/session";
        try {
            return this.makeRequest("POST", path, user, AuthData.class, null);
        } catch (DataAccessException e) {
            return switch (e.getMessage()){
                case "401" -> new AuthData(null,null,"Error: unauthorized");
                default -> throw new IllegalStateException("Unexpected value: " + e.getMessage());
            };
        }
    }

    public ErrorData logout(String authToken){
        String path = "/session";
        try {
            return this.makeRequest("DELETE", path, null, ErrorData.class, authToken);
        } catch (DataAccessException e) {
            return switch (e.getMessage()){
                case "401" -> new ErrorData("Error: unauthorized");
                default -> throw new IllegalStateException("Unexpected value: " + e.getMessage());
            };
        }
    }

    public GamesListRecord listGames(String authToken){
        String path = "/game";
        GamesListRecord gamesListRecord;
        try {
            gamesListRecord = this.makeRequest("GET", path, null, GamesListRecord.class, authToken);
        } catch (DataAccessException e) {
            return switch (e.getMessage()){
                case "401" -> new GamesListRecord(null,"Error: unauthorized");
                default -> throw new IllegalStateException("Unexpected value: " + e.getMessage());
            };
        }
        return gamesListRecord;
    }

    public GameIDRecord createGame(GameName gameName, String authToken) {
        String path = "/game";
        try {
            return this.makeRequest("POST", path, gameName, GameIDRecord.class, authToken);
        } catch (DataAccessException e) {
            return switch (e.getMessage()){
                case "400" -> new GameIDRecord(-1,"Error: bad request");
                case "401" -> new GameIDRecord(-1,"Error: unauthorized");
                default -> throw new IllegalStateException("Unexpected value: " + e.getMessage());
            };
        }
    }

    public ErrorData joinGame(int gameID, String teamColor, String authToken) {
        String path = "/game";

        try {
            return this.makeRequest("PUT", path, new JoinGameRequest(teamColor, gameID), ErrorData.class,authToken);
        } catch (DataAccessException e) {
            return switch (e.getMessage()){
                case "400" -> new ErrorData("Error: bad request");
                case "401" -> new ErrorData("Error: unauthorized");
                case "403" -> new ErrorData("Error: already taken");
                default -> throw new IllegalStateException("Unexpected value: " + e.getMessage());
            };
        }
    }

    public ErrorData clear(){
        String path = "/db";
        try{
            return this.makeRequest("DELETE", path, null, ErrorData.class, null);
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws DataAccessException {
        HttpURLConnection http;
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);


            if (authToken != null) {
                writeHeader(request, http, authToken);
            }
            writeBody(request, http);
            http.connect();
            //throwIfNotSuccessful(http);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return readBody(http, responseClass);
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

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws DataAccessException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) { 
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
            catch(IOException e){
                try {
                    throw new DataAccessException(String.valueOf(http.getResponseCode()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
