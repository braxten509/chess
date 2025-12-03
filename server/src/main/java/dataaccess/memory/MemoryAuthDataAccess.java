package dataaccess.memory;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import model.AuthData;

public class MemoryAuthDataAccess implements AuthDataAccess {

  // authToken, username
  Map<String, String> auths = new HashMap<>();

  private static String generateToken() {
    return UUID.randomUUID().toString();
  }

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
  public AuthData getAuthData(String authToken) throws DataAccessException {
    if (Objects.equals(authToken, "") || authToken == null || auths.get(authToken) == null) {
      return null;
    }
    return new AuthData(authToken, auths.get(authToken));
  }

  @Override
  public void clear() throws DataAccessException {
    auths.clear();
  }
}
