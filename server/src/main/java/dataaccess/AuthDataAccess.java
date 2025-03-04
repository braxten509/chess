package dataaccess;

public interface AuthDataAccess {
  String createAuth(String username) throws DataAccessException;
  boolean removeAuth(String authToken) throws DataAccessException;
  void clear() throws DataAccessException;
  String getAuth(String username) throws DataAccessException;
}
