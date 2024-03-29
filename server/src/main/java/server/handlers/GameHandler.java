package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import model.GameData;
import server.handlers.records.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Collection;

public class GameHandler {
    public JsonObject createGame (Request request , Response response) {

        GameService gameService = new GameService();

        Gson serializer = new Gson();
        GameName gameNameRecord = serializer.fromJson(request.body(), GameName.class);

        try {
            String authToken = request.headers("authorization");

            // Create a game using the game service
            int gameId = gameService.createGame(gameNameRecord.gameName(), authToken);

            // Create a GameIDRecord object
            GameIDRecord gameIDRecord = new GameIDRecord(gameId, null);

            // Serialize the GameIDRecord to JSON
            JsonObject jsonResponse;
            response.type("application/json");

            response.body(serializer.toJson(gameIDRecord));
            String jsonString = serializer.toJson(gameIDRecord);
            jsonResponse = serializer.fromJson(jsonString, JsonObject.class);
            return jsonResponse;

        } catch (DataAccessException e) {
            String jsonString = serializer.toJson(new GameIDRecord(-1, e.getMessage()));
            response.body(serializer.toJson(new GameIDRecord(-1, e.getMessage())));
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);
            response.status(401);
            return jsonObject;
        }
    }


    public Object listGames (Request request, Response response){
        GameService gameService = new GameService();

        Gson serializer = new Gson();
        try{
            String authToken = request.headers("authorization");
            Collection<GameData> gamesList =  gameService.listGames(authToken);

            GamesListRecord gamesListObject = new GamesListRecord(gamesList, null);

            String jsonString = serializer.toJson(gamesListObject);

            // Set response type and body
            response.type("application/json");
            response.body(jsonString);

            // Return the JSON response
            return jsonString;

        }catch (DataAccessException e){
            String jsonString = serializer.toJson(new GamesListRecord(null, e.getMessage()));
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);
            response.status(401);
            response.body(jsonString);
            return jsonObject;
        }
    }

    public Object joinGame (Request request, Response response){
        GameService gameService = new GameService();
        Gson serializer = new Gson();
        try{
            String authToken = request.headers("authorization");
            JoinGameRequest gameRequest = serializer.fromJson(request.body(), JoinGameRequest.class);

            gameService.joinGame(gameRequest.playerColor(), gameRequest.gameID(), authToken);

            String jsonString = serializer.toJson(new ErrorData(null));
            response.status(200);


            // Set response type and body
            response.type("application/json");
            response.body(jsonString);

            // Return the JSON response
            return jsonString;

        }catch (DataAccessException e){
            ErrorData errorData = new ErrorData(e.getMessage());
            String jsonString = serializer.toJson(new ErrorData(errorData.message()));
            response.type("application/json");

            if (e.getMessage().charAt(e.getMessage().length() -1) == 'n') {
                response.status(403);
            }
            else if (e.getMessage().charAt(e.getMessage().length() -1) == 't') {
                response.status(400);
            }
            else{
                response.status(401);
            }
            response.body(jsonString);
            return jsonString;
        }

    }
}
