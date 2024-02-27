package server;

import server.handlers.LoginHandler;
import spark.*;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        post("/user", ((request, response) ->{
            LoginHandler loginHandler = new LoginHandler();
            return loginHandler.login(request.body());
        }));


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
