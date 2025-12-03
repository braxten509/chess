package dataaccess;

import java.util.Collection;
import model.UserData;

public interface UserDataAccess {
  UserData getUser(String username) throws DataAccessException;

  UserData createUser(String username, String password, String email) throws DataAccessException;

  void clear() throws DataAccessException;

  Collection<UserData> listUsers() throws DataAccessException;
}
