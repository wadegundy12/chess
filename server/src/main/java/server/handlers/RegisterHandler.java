package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import model.UserData;
import service.UserService;

public class RegisterHandler {

    public String register (JsonObject jsonObject){
        AuthData authData;
        UserService userService = new UserService();

        Gson serializer = new Gson();
        UserData userData = serializer.fromJson(jsonObject, UserData.class);

        authData = userService.register(userData);
        return serializer.toJson(authData);

    }
}
