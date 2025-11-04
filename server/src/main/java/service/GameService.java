package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import java.util.ArrayList;
import java.util.Objects;
import model.AuthData;
import model.CreateGameRequest;
import model.GameData;
import model.JoinGameRequest;

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
          createGameRequest.authToken() == null ||
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

  public GameData getGame(int gameID) throws DataAccessException {
    return gameDataAccess.getGame(gameID);
  }

  public void joinGame(JoinGameRequest joinGameRequest)
    throws DataAccessException {
    if (
      joinGameRequest == null ||
      joinGameRequest.authToken().isEmpty() ||
      (!Objects.equals(joinGameRequest.playerColor(), "WHITE") &&
        (!Objects.equals(joinGameRequest.playerColor(), "BLACK")))
    ) {
      throw new DataAccessException("bad request");
    }

    AuthData authData = authDataAccess.getAuthData(joinGameRequest.authToken());

    if (authData == null) {
      throw new DataAccessException("unauthorized");
    }

    GameData gameData = getGame(joinGameRequest.gameID());

    if (gameData == null) {
      throw new DataAccessException("bad request");
    }

    String teamColor = joinGameRequest.playerColor();

    if (
      gameData.blackUsername() != null && Objects.equals(teamColor, "BLACK")
    ) {
      throw new DataAccessException("already taken");
    }

    if (
      gameData.whiteUsername() != null && Objects.equals(teamColor, "WHITE")
    ) {
      throw new DataAccessException("already taken");
    }

    String playerUsername = authData.username();
    gameDataAccess.joinGame(
      joinGameRequest.playerColor(),
      joinGameRequest.gameID(),
      playerUsername
    );
  }

  public ArrayList<GameData> listGames(String authToken)
    throws DataAccessException {
    if (authToken == null || authToken.isEmpty()) {
      throw new DataAccessException("unauthorized");
    }

    AuthData authData = authDataAccess.getAuthData(authToken);

    if (authData == null) {
      throw new DataAccessException("unauthorized");
    }

    return gameDataAccess.listGames();
  }
}
