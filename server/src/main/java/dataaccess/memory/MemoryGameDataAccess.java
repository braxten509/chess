package dataaccess.memory;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import java.util.HashMap;
import java.util.Map;
import model.GameData;

public class MemoryGameDataAccess implements GameDataAccess {

  Map<String, GameData> games = new HashMap<>();
  int currentId = 0;

  @Override
  public void clear() throws DataAccessException {}

  @Override
  public int createNewGame(String gameName) throws DataAccessException {
    currentId += 1;
    games.put(
      gameName,
      new GameData(currentId, null, null, gameName, new ChessGame())
    );
    return currentId;
  }
}
