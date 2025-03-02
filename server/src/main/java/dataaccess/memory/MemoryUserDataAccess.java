package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryUserDataAccess implements UserDataAccess {

    Map<String, UserData> users = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public AuthData createUser(String username, String encryptedPassword, String email) throws DataAccessException {
        users.put(username, new UserData(username, encryptedPassword, email));
        return new AuthData(generateToken(), username);
    }
}
