package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.database.DatabaseGameDataAccess;
import dataaccess.database.DatabaseUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseGameDataAccessTests {

  private DatabaseUserDataAccess databaseUserDataAccess;
  private DatabaseGameDataAccess databaseGameDataAccess;
  private final UserData userData = new UserData(
    "testusername",
    "testPassword",
    "testEmail@email.com"
  );
  private int gameID;

  @BeforeEach
  void reset() throws DataAccessException {
    this.databaseUserDataAccess = new DatabaseUserDataAccess();
    this.databaseGameDataAccess = new DatabaseGameDataAccess();

    databaseUserDataAccess.clear();
    databaseGameDataAccess.clear();

    databaseUserDataAccess.createUser(
      userData.username(),
      userData.password(),
      userData.email()
    );
    this.gameID = databaseGameDataAccess.createNewGame("gameOne");
  }

  @Test
  void clear() throws DataAccessException {
    databaseUserDataAccess.clear();

    assertEquals(0, databaseUserDataAccess.listUsers().size());
  }

  @Test
  void createNewGame() throws DataAccessException {
    databaseGameDataAccess.createNewGame("newGame");

    assertEquals(2, databaseGameDataAccess.listGames().size());
  }

  @Test
  void createNewGameFail() {
    assertThrows(DataAccessException.class, () ->
      databaseGameDataAccess.createNewGame("newGame );")
    );
  }

  @Test
  void joinGame() throws DataAccessException {
    databaseGameDataAccess.joinGame("WHITE", gameID, "Player1");

    assertEquals(
      "Player1",
      databaseGameDataAccess.listGames().getFirst().whiteUsername()
    );
  }

  @Test
  void joinGameFail() {
    assertThrows(DataAccessException.class, () ->
      databaseGameDataAccess.joinGame("WHITE", gameID + 143, "Player1")
    );
  }

  @Test
  void getGame() throws DataAccessException {
    assertNotNull(databaseGameDataAccess.getGame(gameID));
  }

  @Test
  void getGameFail() throws DataAccessException {
    assertNull(databaseGameDataAccess.getGame(gameID + 573));
  }

  @Test
  void listGames() throws DataAccessException {
    databaseGameDataAccess.createNewGame("secondGame");

    assertEquals(
      "gameOne",
      databaseGameDataAccess.listGames().getFirst().gameName()
    );
    assertEquals(
      "secondGame",
      databaseGameDataAccess.listGames().getLast().gameName()
    );
  }
}
