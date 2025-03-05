package dataaccess.memory;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import java.util.HashMap;
import java.util.Map;
import model.GameData;

public class MemoryGameDataAccess implements GameDataAccess {

  // id and GameData
  Map<Integer, GameData> games = new HashMap<>();
  int currentId = 0;

  @Override
  public void clear() throws DataAccessException {}

  @Override
  public int createNewGame(String gameName) throws DataAccessException {
    currentId += 1;
    games.put(
      currentId,
      new GameData(currentId, null, null, gameName, new ChessGame())
    );
    return currentId;
  }

  @Override
  public GameData getGame(int gameID) {
    return games.get(gameID);
  }

  @Override
  public void joinGame(String playerColor, int gameID, String playerUsername) {
    GameData joiningGame = games.get(gameID);
    String gameName = joiningGame.gameName();
    ChessGame game = joiningGame.game();

    if (playerColor.equals("WHITE"))
    {
      String blackUsername = joiningGame.blackUsername();
      games.put(gameID, new GameData(gameID, playerUsername, blackUsername, gameName, game));
    }
    else
    {
      String whiteUsername = joiningGame.whiteUsername();
      games.put(gameID, new GameData(gameID, whiteUsername, playerUsername, gameName, game));
    }
  }
}
