package dataaccess.database;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

public class DatabaseAuthDataAccess implements AuthDataAccess {

  @Override
  public String createAuth(String username) throws DataAccessException {
    return "";
  }

  @Override
  public boolean removeAuth(String authToken) throws DataAccessException {
    return false;
  }

  @Override
  public void clear() throws DataAccessException {}

  @Override
  public AuthData getAuthData(String authToken) throws DataAccessException {
    return null;
  }
}
