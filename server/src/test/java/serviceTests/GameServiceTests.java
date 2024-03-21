package serviceTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.Collection;



public class GameServiceTests {
    @BeforeEach
    public void clearDAOs(){
        GameService tempGameService = new GameService();
        tempGameService.clear();
        UserService tempUserService = new UserService();
        tempUserService.clear();
    }


    //NOTE: This also tests listGames();
    @Test
    public void createValidGame() throws DataAccessException{
        UserService userService = new UserService();
        GameService newGameService = new GameService();
        AuthData authData1 = userService.register(new UserData("Wade", "", "", null));
        AuthData authData2 = userService.register(new UserData("Nathan", "", "", null));

        int game1ID = newGameService.createGame("wade's game", authData2.authToken());
        int game2ID = newGameService.createGame("nathan's game", authData2.authToken());


        Assertions.assertEquals(2, newGameService.listGames(authData1.authToken()).size());

    }

    // Checks if game is already made, throws exception
    @Test
    public void gameAlreadyCreated() throws DataAccessException {
        UserService userService = new UserService();

        GameService newGameService = new GameService();

        AuthData authData1 = userService.register(new UserData("Wade", "", "", null));
        newGameService.createGame("wade's game", authData1.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> { newGameService.createGame("wade's game", authData1.authToken());});
    }


    @Test
    public void whiteJoinsGame() throws DataAccessException{
        GameService newGameService = new GameService();
        UserService userService = new UserService();

        ChessGame tempGame = new ChessGame();

        AuthData authData1 = userService.register(new UserData("Wade", "", "", null));
        AuthData authData2 = userService.register(new UserData("Nathan", "", "", null));

        int gameID = newGameService.createGame("wade's game", authData1.authToken());
        newGameService.createGame("nathan's game", authData2.authToken());



        newGameService.joinGame("WHITE", gameID, authData1.authToken());
        Assertions.assertEquals(new GameData(gameID, "Wade", null, "wade's game", tempGame, null), newGameService.getGameData(gameID));
    }

    @Test
    public void bothPlayersJoin() throws DataAccessException {
        GameService newGameService = new GameService();
        UserService userService = new UserService();

        AuthData authData1 = userService.register(new UserData("Wade", "", "", null));
        AuthData authData2 = userService.register(new UserData("Nathan", "", "", null));

        ChessGame tempGame = new ChessGame();
        int gameID = newGameService.createGame("wade's game", authData1.authToken());
        newGameService.createGame("another game", authData2.authToken());





        newGameService.joinGame("WHITE", gameID, authData1.authToken());
        newGameService.joinGame("BLACK", gameID, authData2.authToken());


        Assertions.assertEquals(new GameData(gameID, "Wade", "Nathan", "wade's game", tempGame, null), newGameService.getGameData(gameID));
    }

    @Test
    public void listZeroGames() throws DataAccessException{
        UserService userService = new UserService();
        AuthData authData1 = userService.register(new UserData("Wade", "", "", null));


        GameService newGameService = new GameService();
        Collection<GameData> tempGames = new ArrayList<>();

        Assertions.assertEquals(0, newGameService.listGames(authData1.authToken()).size());
        Assertions.assertEquals(tempGames, newGameService.listGames(authData1.authToken()));


    }




}
