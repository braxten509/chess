package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

  private static Server server;
  static ServerFacade facade;
  private static String authToken;

  @BeforeAll
  public static void init() {
    server = new Server();
    var port = server.run(0);
    System.out.println("Started test HTTP server on " + port);
    facade = new ServerFacade(port);
  }

  @AfterAll
  static void stopServer() {
    server.stop();
  }

  @BeforeEach
  void reset() {
    facade.clearDatabase();
    var authData = facade.registerUser("player1", "password", "p1@email.com");
    authToken = authData.authToken();
  }

  @Test
  void registerUser() {
    assertTrue(authToken.length() > 10);
  }

  @Test
  void registerUserFail() {
    assertThrows(Exception.class, () -> facade.registerUser("player1", "password", "p1@email.com"));
  }

  @Test
  void loginUser() {
    AuthData authData = facade.loginUser("player1", "password");
    assertEquals("player1", authData.username());
  }

  @Test
  void loginUserFail() {
    assertThrows(Exception.class, () -> facade.loginUser("player2", "passwordNonexistent"));
  }

  @Test
  void createGame() {
    facade.createGame(authToken, "TestGame");
    ListGamesResult listGamesResult = facade.listGames(authToken);
    String gameName = "";
    for (GameData game : listGamesResult.games()) {
      gameName = game.gameName();
    }
    assertEquals("TestGame", gameName);
  }

  @Test
  void createGameFail() {
    String fakeAuthToken = "fake";
    assertThrows(Exception.class, () -> facade.createGame(fakeAuthToken, "TestGame"));
  }

  @Test
  void joinGameAndJoinFailTest() {
    CreateGameResult createGameResult = facade.createGame(authToken, "TestGame");
    facade.joinGame(authToken, "WHITE", createGameResult.gameID());
    assertThrows(Exception.class, () -> facade.joinGame(authToken, "WHITE", createGameResult.gameID()));
  }

  @Test
  void listGames() {
    facade.createGame(authToken, "TestGame");
    facade.createGame(authToken, "TestGame2");

    ListGamesResult listGamesResult = facade.listGames(authToken);
    assertEquals(2, listGamesResult.games().size());
  }

  @Test
  void listGamesFail() {
    String fakeAuth = "hi";
    facade.createGame(authToken, "TestGame");
    facade.createGame(authToken, "TestGame2");

    assertThrows(Exception.class, () -> facade.listGames(fakeAuth));
  }

  @Test
  void logoutUser() {
    AuthData authData = facade.loginUser("player1", "password");
    String newAuthToken = authData.authToken();

    facade.logoutUser(newAuthToken);
    assertThrows(Exception.class, () -> facade.logoutUser(newAuthToken));
  }

  @Test
  void logoutUserFail() {
    assertThrows(Exception.class, () -> facade.logoutUser("fakeAuth"));
  }

  @Test
  void clearDatabase() {
    facade.createGame(authToken, "game1");
    facade.createGame(authToken, "game2");
    facade.createGame(authToken, "game3");

    facade.clearDatabase();

    RegisterResult registerResult = facade.registerUser("test", "test", "test@test.com");

    ListGamesResult listGamesResult = facade.listGames(registerResult.authToken());

    assertEquals(0, listGamesResult.games().size());
  }

}
