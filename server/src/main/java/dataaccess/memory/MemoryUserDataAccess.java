package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import java.util.*;
import model.UserData;

public class MemoryUserDataAccess implements UserDataAccess {

  Map<String, UserData> users = new HashMap<>();

  private static String generateToken() {
    return UUID.randomUUID().toString();
  }

  @Override
  public UserData getUser(String username) {
    return users.get(username);
  }

  @Override
  public UserData createUser(String username, String password, String email)
    throws DataAccessException {
    users.put(username, new UserData(username, password, email));
    return users.get(username);
  }

  @Override
  public void clear() throws DataAccessException {
    users.clear();
  }

  @Override
  public Collection<UserData> listUsers() throws DataAccessException {
    return users.values();
  }
}
