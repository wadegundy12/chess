package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {

    public JsonObject login (Request request ,Response response) throws DataAccessException {

        AuthData authData;
        UserService userService = new UserService();
        Gson serializer = new Gson();

        UserData userData = serializer.fromJson(request.body(), UserData.class);

        authData = userService.login(userData);
        response.type("application/json");
        response.body(serializer.toJson(authData));
        JsonObject jsonResponse;

        String jsonString = serializer.toJson(authData);
        jsonResponse = serializer.fromJson(jsonString, JsonObject.class);

        return jsonResponse;

    }
}
