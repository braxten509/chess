package service;

import chess.ChessGame;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryGameDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameServiceTests {
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    void clear() throws Exception {
        MemoryAuthDataAccess authDataAccess = new MemoryAuthDataAccess();
        MemoryGameDataAccess gameDataAccess = new MemoryGameDataAccess();
        MemoryUserDataAccess userDataAccess = new MemoryUserDataAccess();

        this.gameService = new GameService(gameDataAccess, authDataAccess);
        this.userService = new UserService(userDataAccess, authDataAccess);
    }

    @Test
    void clearDataBase() throws DataAccessException {
        userService.registerUser(new RegisterRequest("username", "password", "email"));
        AuthData authData = userService.loginUser(new LoginRequest("username", "password"));
        gameService.createGame(new CreateGameRequest(authData.authToken(), "gameName"));
    }
}
