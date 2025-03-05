package dataaccess;

import model.GameData;

public interface GameDataAccess {
  void clear() throws DataAccessException;
  int createNewGame(String gameName) throws DataAccessException;
  GameData getGame(int gameID) throws DataAccessException;
  void joinGame(String playerColor, int gameID, String playerUsername) throws DataAccessException;
}
