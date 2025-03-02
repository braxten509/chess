package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDataAccess {

    UserData getUser(String username) throws DataAccessException;
    AuthData createUser(String username, String encryptedPassword, String email) throws DataAccessException;

}
