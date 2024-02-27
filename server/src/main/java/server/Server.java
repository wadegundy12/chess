package server;

import server.handlers.ClearHandler;
import server.handlers.LoginHandler;
import server.handlers.RegisterHandler;
import spark.*;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        post("/user", ((request, response) -> new RegisterHandler().register(request,response)));

        delete("/db", (((request, response) -> new ClearHandler().clear(response))));



        Spark.awaitInitialization();
        return Spark.port();
    }

    public static void main(String args[]){
        Server server = new Server();
        server.run(8080);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
