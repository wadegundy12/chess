package serviceTests;

import chess.InvalidMoveException;
import dataAccess.DataAccessException;
import dataAccess.UserDao;
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


    }


}
