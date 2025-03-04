package dataaccess.memory;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDataAccess implements AuthDataAccess {

  private static String generateToken() {
    return UUID.randomUUID().toString();
  }

  // authToken, username
  Map<String, String> auths = new HashMap<>();

  @Override
  public String createAuth(String username) throws DataAccessException {
    String authToken = generateToken();
    auths.put(authToken, username);
    return authToken;
  }

  @Override
  public boolean removeAuth(String authToken) throws DataAccessException {
    if (auths.containsKey(authToken)) {
      auths.remove(authToken);
      return true;
    }
      return false;
  }

  @Override
  public String getAuth(String username) throws DataAccessException {
    for (Map.Entry<String, String> entry : auths.entrySet()) {
      if (Objects.equals(entry.getValue(), username)) {
        return entry.getKey();
      }
    }
    return null;
  }

  @Override
  public void clear() throws DataAccessException {
    auths.clear();
  }
}
