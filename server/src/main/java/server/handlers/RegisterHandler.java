package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    public JsonObject register (Request request , Response response){
        AuthData authData;
        UserService userService = new UserService();
        Gson serializer = new Gson();

        UserData userData = serializer.fromJson(request.body(), UserData.class);
        authData = userService.register(userData);

        response.type("application/json");
        response.body(serializer.toJson(authData));
        JsonObject jsonResponse;


        String jsonString = serializer.toJson(authData);
        jsonResponse = serializer.fromJson(jsonString, JsonObject.class);

        return jsonResponse;

    }
}
