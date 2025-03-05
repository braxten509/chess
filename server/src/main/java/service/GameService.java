package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import java.util.Objects;
import model.AuthData;
import model.CreateGameRequest;

public class GameService {

  private final GameDataAccess gameDataAccess;
  private final AuthDataAccess authDataAccess;

  public GameService(
    GameDataAccess gameDataAccess,
    AuthDataAccess authDataAccess
  ) {
    this.gameDataAccess = gameDataAccess;
    this.authDataAccess = authDataAccess;
  }

  public void clearDataAccess() throws DataAccessException {
    gameDataAccess.clear();
  }

  public int createGame(CreateGameRequest createGameRequest)
    throws DataAccessException {
    if (
      createGameRequest == null ||
      Objects.equals(createGameRequest.gameName(), "") ||
      createGameRequest.authToken().isEmpty()
    ) {
      throw new DataAccessException("bad request");
    }

    AuthData authData = authDataAccess.getAuthData(
      createGameRequest.authToken()
    );

    if (authData == null) {
      throw new DataAccessException("unauthorized");
    }

    return gameDataAccess.createNewGame(createGameRequest.gameName());
  }
}
