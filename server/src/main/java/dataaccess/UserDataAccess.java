package dataaccess;

import java.util.Collection;
import model.AuthData;
import model.UserData;

public interface UserDataAccess {
  UserData getUser(String username) throws DataAccessException;
  AuthData createUser(String username, String encryptedPassword, String email)
    throws DataAccessException;
  void clear() throws DataAccessException;
  Collection<UserData> listUsers() throws DataAccessException;
}
