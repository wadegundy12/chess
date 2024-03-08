package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import dataAccess.SQLGameDAO;
import dataAccess.SQLUserDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {

    @BeforeEach
    public void clearTables(){
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    public void testClear() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameID = gameDAO.createGame("Wade");
        gameDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.getGame(gameID);
        });
    }

    @Test
    public void addGame() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameID = gameDAO.createGame("Wade");
        GameData gameData = gameDAO.getGame(gameID);
        Assertions.assertEquals(gameID, gameData.getGameID());
        Assertions.assertEquals("Wade", gameData.getGameName());
    }

    @Test
    public void gameExistsAlready() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameID = gameDAO.createGame("Wade");
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame("Wade");
        });
    }

    @Test
    public void getGame() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameIDOne = gameDAO.createGame("Wade");
        int gameIDTwo = gameDAO.createGame("Test");
        GameData gameData = gameDAO.getGame(gameIDTwo);
        Assertions.assertEquals(gameIDTwo, gameData.getGameID());
        Assertions.assertEquals("Test", gameData.getGameName());
    }

    @Test
    public void failedGetGame() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameIDOne = gameDAO.createGame("Wade");
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.getGame(12345);
        });
    }

    @Test
    public void listGames() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameIDOne = gameDAO.createGame("Wade");
        int gameIDTwo = gameDAO.createGame("Test");
        Assertions.assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    public void emptyGameList() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        Assertions.assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    public void updateGame() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameIDOne = gameDAO.createGame("Wade");
        int gameIDTwo = gameDAO.createGame("Test");
        ChessGame tempGame = gameDAO.getGame(gameIDOne).getGame();
        GameData gameData = new GameData(gameIDOne, "newWhiteUsername", null, null, tempGame);
        gameDAO.updateGame(gameIDOne, gameData);
        Assertions.assertEquals(gameData, gameDAO.getGame(gameIDOne));
    }

    @Test
    public void updateWrongGame() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameIDOne = gameDAO.createGame("Wade");
        ChessGame tempGame = gameDAO.getGame(gameIDOne).getGame();
        GameData gameData = new GameData(gameIDOne, "newWhiteUsername", null, null, tempGame);
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(123, gameData);
        });

    }










}
