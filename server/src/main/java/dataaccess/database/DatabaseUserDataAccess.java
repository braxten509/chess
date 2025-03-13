package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDataAccess;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import model.UserData;

public class DatabaseUserDataAccess implements UserDataAccess {

  @Override
  public UserData getUser(String username) throws DataAccessException {
    return null;
  }

  @Override
  public UserData createUser(
    String username,
    String encryptedPassword,
    String email
  ) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {} catch (
      SQLException e
    ) {}
    return null;
  }

  @Override
  public void clear() throws DataAccessException {}

  @Override
  public Collection<UserData> listUsers() throws DataAccessException {
    return List.of();
  }
}
