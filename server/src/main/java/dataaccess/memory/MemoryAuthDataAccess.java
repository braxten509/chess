package dataaccess.memory;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import java.util.HashMap;
import java.util.Map;
import model.AuthData;
import model.UserData;

public class MemoryAuthDataAccess implements AuthDataAccess {

  Map<String, String> auths = new HashMap<>();

  @Override
  public void createAuth(AuthData authData) throws DataAccessException {
    auths.put(authData.authToken(), authData.username());
  }

  @Override
  public void clear() throws DataAccessException {
    auths.clear();
  }
}
