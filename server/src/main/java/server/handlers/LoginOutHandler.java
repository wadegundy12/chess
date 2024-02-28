package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginOutHandler {

    public JsonObject login (Request request ,Response response) {

        AuthData authData;
        UserService userService = new UserService();
        Gson serializer = new Gson();


        try {
            UserData userData = serializer.fromJson(request.body(), UserData.class);

            authData = userService.login(userData);
            response.type("application/json");
            response.body(serializer.toJson(authData));
            JsonObject jsonResponse;

            String jsonString = serializer.toJson(authData);
            jsonResponse = serializer.fromJson(jsonString, JsonObject.class);

            return jsonResponse;
        } catch (DataAccessException e) {
            ErrorData errorData = new ErrorData(e.getMessage());
            response.status(401);
            String jsonString = serializer.toJson(errorData);
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);
            response.status(401);
            return jsonObject;
        }
    }

    public JsonObject logout (Request request ,Response response) {

        String authToken;
        AuthData authData;
        UserService userService = new UserService();
        Gson serializer = new Gson();


        try {
            UserData userData = serializer.fromJson(request.body(), UserData.class);
            authToken = serializer.fromJson(request.headers("authorization"), String.class);
            authData = new AuthData(authToken, userData.username());


            userService.logout(authData);
            response.type("application/json");


            return new JsonObject();
        } catch (DataAccessException e) {
            ErrorData errorData = new ErrorData(e.getMessage());
            response.status(401);
            String jsonString = serializer.toJson(errorData);
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);
            response.status(401);
            return jsonObject;
        }

    }
}
