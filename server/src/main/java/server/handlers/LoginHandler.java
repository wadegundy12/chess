package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;

public class LoginHandler {

    public String login (JsonObject jsonObject){
        AuthData authData;
        UserService userService = new UserService();

        Gson serializer = new Gson();
        UserData userData = serializer.fromJson(jsonObject, UserData.class);

        try {
            authData = userService.login(userData);
            return serializer.toJson(authData);
        } catch (DataAccessException e) {
            ErrorData error = new ErrorData(e.toString());
            return serializer.toJson(error);

        }



    }
}
