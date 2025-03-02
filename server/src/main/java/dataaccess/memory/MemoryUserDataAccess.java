package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;

import java.util.*;

import model.AuthData;
import model.UserData;

public class MemoryUserDataAccess implements UserDataAccess {

  Map<String, UserData> users = new HashMap<>();

  public static String generateToken() {
    return UUID.randomUUID().toString();
  }

  @Override
  public UserData getUser(String username) throws DataAccessException {
    return users.get(username);
  }

  @Override
  public AuthData createUser(
    String username,
    String encryptedPassword,
    String email
  ) throws DataAccessException {
    users.put(username, new UserData(username, encryptedPassword, email));
    return new AuthData(generateToken(), username);
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
