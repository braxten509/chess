package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

public class UserService {

    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;

    public UserService(MemoryUserDataAccess userDataAccess, MemoryAuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData user = userDataAccess.getUser(registerRequest.username());

        if (user != null) {
            throw new DataAccessException("User already exists!");
        }

        AuthData authData = userDataAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        authDataAccess.createAuth(authData);

        return new RegisterResult(registerRequest.username(), authData.authToken());
    }
}
