package dataaccess;

public interface GameDataAccess {
  void clear() throws DataAccessException;
  int createNewGame(String gameName) throws DataAccessException;
}
