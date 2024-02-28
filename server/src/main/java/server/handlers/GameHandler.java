package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import service.GameService;
import spark.Request;
import spark.Response;

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
            GameIDRecord gameIDRecord = new GameIDRecord(gameId);

            // Serialize the GameIDRecord to JSON
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("gameId", gameIDRecord.gameID());

            // Set response type and body
            response.type("application/json");
            response.body(jsonResponse.toString());

            // Return the JSON response
            return jsonResponse;

        } catch (DataAccessException e) {
            ErrorData errorData = new ErrorData(e.getMessage());
            String jsonString = serializer.toJson(errorData);
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);
            response.status(401);
            return jsonObject;
        }









    }
}
