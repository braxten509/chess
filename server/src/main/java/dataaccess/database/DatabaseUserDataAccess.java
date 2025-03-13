package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.UserData;

import java.util.Collection;
import java.util.List;

public class DatabaseUserDataAccess implements UserDataAccess {
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public UserData createUser(String username, String encryptedPassword, String email) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public Collection<UserData> listUsers() throws DataAccessException {
        return List.of();
    }
}
