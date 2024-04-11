package server;

import server.handlers.ClearHandler;
import server.handlers.GameHandler;
import server.handlers.LoginOutHandler;
import server.handlers.RegisterHandler;
import server.websocket.WebSocketHandler;
import spark.*;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", new WebSocketHandler());

        // Register your endpoints and handle exceptions here.
        post("/user", ((request, response) -> new RegisterHandler().register(request,response)));

        post("/session", ((request, response) -> new LoginOutHandler().login(request,response)));

        delete("/session", (((request, response) -> new LoginOutHandler().logout(request,response) )));

        delete("/db", (((request, response) -> new ClearHandler().clear(response))));

        post("/game", ((request, response) -> new GameHandler().createGame(request,response)));

        get("/game", ((request, response) -> new GameHandler().listGames(request,response)));

        put("/game", ((request, response) -> new GameHandler().joinGame(request,response)));




        Spark.awaitInitialization();
        return Spark.port();
    }

    public static void main(String[] args){
        Server server = new Server();
        server.run(8080);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
