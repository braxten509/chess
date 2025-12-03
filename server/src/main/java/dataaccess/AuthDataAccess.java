package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
  String createAuth(String username) throws DataAccessException;

  boolean removeAuth(String authToken) throws DataAccessException;

  void clear() throws DataAccessException;

  AuthData getAuthData(String authToken) throws DataAccessException;
}
