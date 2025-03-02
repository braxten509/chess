package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.Collection;

public interface UserDataAccess {
  UserData getUser(String username) throws DataAccessException;
  AuthData createUser(String username, String encryptedPassword, String email)
    throws DataAccessException;
  void clear() throws DataAccessException;
  Collection<UserData> listUsers() throws DataAccessException;
}
