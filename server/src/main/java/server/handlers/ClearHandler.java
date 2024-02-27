package server.handlers;

import com.google.gson.JsonObject;
import service.GameService;
import service.UserService;
import spark.Response;

public class ClearHandler {

    public JsonObject clear(Response response){
        GameService gameService = new GameService();
        UserService userService = new UserService();

        gameService.clear();
        userService.clear();


        response.status(200);
        JsonObject jsonResponse = new JsonObject();
        // Set response content type to indicate JSON
        response.type("application/json");
        return jsonResponse;
    }
}
