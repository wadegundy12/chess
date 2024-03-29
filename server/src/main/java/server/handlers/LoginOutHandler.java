package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import server.handlers.records.ErrorData;
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
            String jsonString = serializer.toJson(new AuthData(null,null, errorData.message()));
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);
            response.body(jsonString);
            response.status(401);
            return jsonObject;
        }
    }

    public JsonObject logout (Request request ,Response response) {

        String authToken;
        UserService userService = new UserService();
        Gson serializer = new Gson();


        try {
            JsonObject jsonResponse;

            authToken = serializer.fromJson(request.headers("authorization"), String.class);
            userService.logout(authToken);
            response.type("application/json");
            response.body(serializer.toJson(""));
            String jsonString = serializer.toJson(new ErrorData(null));
            jsonResponse = serializer.fromJson(jsonString, JsonObject.class);

            return jsonResponse;
        } catch (DataAccessException e) {
            ErrorData errorData = new ErrorData(e.getMessage());
            String jsonString = serializer.toJson(new ErrorData(errorData.message()));
            response.body(errorData.message());
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);
            response.status(401);
            return jsonObject;
        }

    }
}
