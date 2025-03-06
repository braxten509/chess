package service;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryGameDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {

  private GameService gameService;
  private AuthData authData;
  private int gameID;

  @BeforeEach
  void clear() throws DataAccessException {
    MemoryAuthDataAccess authDataAccess = new MemoryAuthDataAccess();
    MemoryGameDataAccess gameDataAccess = new MemoryGameDataAccess();
    MemoryUserDataAccess userDataAccess = new MemoryUserDataAccess();

    this.gameService = new GameService(gameDataAccess, authDataAccess);
    UserService userService = new UserService(userDataAccess, authDataAccess);

    userService.registerUser(
      new RegisterRequest("username", "password", "email")
    );
    this.authData = userService.loginUser(
      new LoginRequest("username", "password")
    );
    this.gameID = gameService.createGame(
      new CreateGameRequest(authData.authToken(), "gameName")
    );
  }

  // will always succeed so no fail save test
  @Test
  void clearDataBase() throws DataAccessException {
    gameService.clearDataAccess();
    assertEquals(0, gameService.listGames(authData.authToken()).size());
  }

  @Test
  void createGame() throws DataAccessException {
    assertEquals(1, gameService.listGames(authData.authToken()).size());
  }

  @Test
  void createGameFail() {
    assertThrows(DataAccessException.class, () ->
      gameService.createGame(new CreateGameRequest("invalidToken", "gameName"))
    );
  }

  @Test
  void getGame() throws DataAccessException {
    GameData game = gameService.getGame(gameID);

    assertNotNull(game);
  }

  @Test
  void getGameFail() throws DataAccessException {
    assertNull(gameService.getGame(12345));
  }

  @Test
  void joinGame() throws DataAccessException {
    gameService.joinGame(
      new JoinGameRequest(authData.authToken(), "WHITE", gameID)
    );

    GameData game = gameService.getGame(gameID);
    assertNotNull(game.whiteUsername());
  }

  @Test
  void joinGameFail() {
    assertThrows(DataAccessException.class, () ->
      gameService.joinGame(new JoinGameRequest("", "", 123))
    );
  }

  @Test
  void listGames() throws DataAccessException {
    gameService.createGame(
      new CreateGameRequest(authData.authToken(), "gameName")
    );
    gameService.createGame(
      new CreateGameRequest(authData.authToken(), "gameNameA")
    );

    assertEquals(3, gameService.listGames(authData.authToken()).size());
  }
}
