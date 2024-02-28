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

public class RegisterHandler {

    public Object register (Request request , Response response){
        AuthData authData;
        String jsonString;
        UserService userService = new UserService();
        Gson serializer = new Gson();

        UserData userData = serializer.fromJson(request.body(), UserData.class);
        JsonObject jsonResponse;
        try {
            authData = userService.register(userData);

            response.type("application/json");
            response.body(serializer.toJson(authData));


            jsonString = serializer.toJson(authData);
            jsonResponse = serializer.fromJson(jsonString, JsonObject.class);
        } catch (DataAccessException e) {
            ErrorData errorData = new ErrorData(e.getMessage());
            jsonString = serializer.toJson(errorData);
            JsonObject jsonObject = serializer.fromJson(jsonString, JsonObject.class);

            if (e.getMessage().charAt(e.getMessage().length() -1) == 'n') {
                response.status(403);
            }
            else{
                response.status(400);
            }
            return jsonObject;
        }

        return jsonString;

    }
}
