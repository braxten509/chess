package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import java.util.ArrayList;
import model.GameData;

public class DatabaseGameDataAccess implements GameDataAccess {

  @Override
  public void clear() throws DataAccessException {}

  @Override
  public int createNewGame(String gameName) throws DataAccessException {
    return 0;
  }

  @Override
  public GameData getGame(int gameID) throws DataAccessException {
    return null;
  }

  @Override
  public void joinGame(String playerColor, int gameID, String playerUsername)
    throws DataAccessException {}

  @Override
  public ArrayList<GameData> listGames() throws DataAccessException {
    return null;
  }
}
