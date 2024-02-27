package serviceTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;


public class GameServiceTests {
    @BeforeAll
    public static void clearDAOs(){
        GameService tempGameService = new GameService();
        tempGameService.clear();
        UserService tempUserService = new UserService();
        tempUserService.clear();
    }

    @Test
    public void createValidGame() throws DataAccessException{
        GameService newGameService = new GameService();
        ChessGame tempGame = new ChessGame();
        newGameService.createGame("wade's game");
        newGameService.createGame("another game");

        Assertions.assertEquals(2, newGameService.listGames().size());
    }

    // Checks if game is already made, throws exception
    @Test
    public void gameAlreadyCreated() throws DataAccessException {
        GameService newGameService = new GameService();
        newGameService.createGame("wade's game");
        Assertions.assertThrows(DataAccessException.class, () -> { newGameService.createGame("wade's game");});
    }


    @Test
    public void whiteJoinsGame() throws DataAccessException{
        GameService newGameService = new GameService();
        UserService userService = new UserService();

        ChessGame tempGame = new ChessGame();
        int gameID = newGameService.createGame("wade's game");
        newGameService.createGame("another game");

        userService.register(new UserData("Wade", "", ""));


        newGameService.joinGame(ChessGame.TeamColor.WHITE, gameID, "Wade");
        Assertions.assertEquals(new GameData(gameID, "Wade", null, "wade's game", tempGame), newGameService.getGameData(gameID));
    }

    @Test
    public void bothPlayersJoin() throws DataAccessException {
        GameService newGameService = new GameService();
        UserService userService = new UserService();

        ChessGame tempGame = new ChessGame();
        int gameID = newGameService.createGame("wade's game");
        newGameService.createGame("another game");

        userService.register(new UserData("Wade", "", ""));
        userService.register(new UserData("Nathan", "", ""));



        newGameService.joinGame(null, gameID, "Wade");
        newGameService.joinGame(ChessGame.TeamColor.BLACK, gameID, "Nathan");


        Assertions.assertEquals(new GameData(gameID, "Wade", "Nathan", "wade's game", tempGame), newGameService.getGameData(gameID));
    }



}
