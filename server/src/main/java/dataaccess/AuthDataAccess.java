package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
  void createAuth(AuthData authData) throws DataAccessException;
  void clear() throws DataAccessException;
}
