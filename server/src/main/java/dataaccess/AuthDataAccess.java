package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
  String createAuth(String username) throws DataAccessException;
  void clear() throws DataAccessException;
}
