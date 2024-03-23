package clientTests;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.handlers.records.ErrorData;
import server.handlers.records.GameIDRecord;
import server.handlers.records.GameName;
import server.handlers.records.GamesListRecord;
import serverFacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade =  new ServerFacade("http://localhost:8080");;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clear(){
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void register(){
        AuthData authData;
        authData = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData);
    }

    @Test
    public void alreadyRegistered(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        AuthData authData2 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertEquals("Error: already taken", authData2.errorMessage());
    }

    @Test
    public void login(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        serverFacade.logout(authData1.authToken());
        AuthData authData2 = serverFacade.login(new UserData("wade", "wade", null, null));
        Assertions.assertNotNull(authData2);
    }

    @Test
    public void badLogin(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        AuthData authData2 = serverFacade.login(new UserData("wade", "notWade", null, null));
        Assertions.assertEquals("Error: unauthorized", authData2.errorMessage());
    }

    @Test
    public void logout(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        ErrorData errorData = serverFacade.logout(authData1.authToken());
        Assertions.assertNull(errorData.message());
    }

    @Test
    public void badLogout(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        ErrorData errorData = serverFacade.logout("randomString");
        Assertions.assertEquals("Error: unauthorized", errorData.message());
    }

    @Test
    public void registerGame(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        GameIDRecord gameIDRecord = serverFacade.createGame(new GameName("wade"), authData1.authToken());
        Assertions.assertNull(gameIDRecord.errorMessage());
    }

    @Test
    public void failedRegisterGame(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        GameIDRecord gameIDRecord1 = serverFacade.createGame(new GameName("wade"), authData1.authToken());
        GameIDRecord gameIDRecord2 = serverFacade.createGame(new GameName("wade"), authData1.authToken());
        Assertions.assertEquals("Error: unauthorized",gameIDRecord2.errorMessage());
    }

    @Test
    public void listGames(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        GameIDRecord gameIDRecord = serverFacade.createGame(new GameName("wade"), authData1.authToken());
        GamesListRecord gamesListRecord = serverFacade.listGames(authData1.authToken());
        Assertions.assertNull(gamesListRecord.errorMessage());
    }

    @Test
    public void failedListGames(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        GameIDRecord gameIDRecord = serverFacade.createGame(new GameName("wade"), authData1.authToken());
        GamesListRecord gamesListRecord = serverFacade.listGames("randomAuth");
        Assertions.assertEquals("Error: unauthorized", gamesListRecord.errorMessage());
    }

    @Test
    public void joinGame(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        GameIDRecord gameIDRecord = serverFacade.createGame(new GameName("wade"), authData1.authToken());
        GamesListRecord gamesListRecord = serverFacade.listGames(authData1.authToken());
        ErrorData errorData = serverFacade.joinGame(0,"white", authData1.authToken());
        Assertions.assertNull(errorData.message());
    }

    @Test
    public void failedJoinGame(){
        AuthData authData1;
        authData1 = serverFacade.register(new UserData("wade", "wade", "wade", null));
        Assertions.assertNotNull(authData1);
        GameIDRecord gameIDRecord = serverFacade.createGame(new GameName("wade"), authData1.authToken());
        ErrorData errorData = serverFacade.joinGame(0,"white", authData1.authToken());
        Assertions.assertEquals("Error: bad request", errorData.message());
    }

}
