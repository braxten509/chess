package dataaccess.memory;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDataAccess implements AuthDataAccess {

    Map<String, String> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        auths.put(authData.authToken(), authData.username());
    }
}
