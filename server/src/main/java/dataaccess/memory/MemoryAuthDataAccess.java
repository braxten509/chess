package dataaccess.memory;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDataAccess implements AuthDataAccess {

  private static String generateToken() {
    return UUID.randomUUID().toString();
  }

  // username, authToken
  Map<String, String> auths = new HashMap<>();

  @Override
  public String createAuth(String username) throws DataAccessException {
    auths.put(username, generateToken());
    return auths.get(username);
  }

  @Override
  public void clear() throws DataAccessException {
    auths.clear();
  }
}
