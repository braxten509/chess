package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import java.util.Collection;
import java.util.Objects;
import model.*;

public class UserService {

  private final UserDataAccess userDataAccess;
  private final AuthDataAccess authDataAccess;

  public UserService(
    MemoryUserDataAccess userDataAccess,
    MemoryAuthDataAccess authDataAccess
  ) {
    this.userDataAccess = userDataAccess;
    this.authDataAccess = authDataAccess;
  }

  public void clearDataAccess() throws DataAccessException {
    userDataAccess.clear();
    authDataAccess.clear();
  }

  public UserData getUser(String username) throws DataAccessException {
    return userDataAccess.getUser(username);
  }

  public Collection<UserData> listUsers() throws DataAccessException {
    return userDataAccess.listUsers();
  }

  public LoginResult loginUser(LoginRequest loginRequest)
    throws DataAccessException {
    UserData user = userDataAccess.getUser(loginRequest.username());

    if (user == null) {
      throw new DataAccessException("unauthorized");
    }

    if (user.username() == null || user.password() == null) {
      throw new DataAccessException("unauthorized");
    }

    if (!Objects.equals(loginRequest.password(), user.password())) {
      throw new DataAccessException("unauthorized");
    }

    String authToken = authDataAccess.createAuth(user.username());

    return new LoginResult(authToken, user.username());
  }

  public void logoutUser(String authToken) throws DataAccessException {
    boolean success = authDataAccess.removeAuth(authToken);
    if (!success) {
      throw new DataAccessException("unauthorized");
    }
  }

  public RegisterResult registerUser(RegisterRequest registerRequest)
    throws DataAccessException {
    if (
      registerRequest.username() == null ||
      registerRequest.password() == null ||
      registerRequest.email() == null
    ) {
      throw new DataAccessException("bad request");
    }

    UserData user = userDataAccess.getUser(registerRequest.username());

    if (user != null) {
      throw new DataAccessException("already taken");
    }

    user = userDataAccess.createUser(
      registerRequest.username(),
      registerRequest.password(),
      registerRequest.email()
    );

    String authToken = authDataAccess.createAuth(user.username());

    return new RegisterResult(user.username(), authToken);
  }
}
