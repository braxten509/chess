package service;

import org.mindrot.jbcrypt.BCrypt;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import java.util.Collection;
import model.*;
import spark.utils.StringUtils;

public class UserService {

  private final UserDataAccess userDataAccess;
  private final AuthDataAccess authDataAccess;

  public UserService(
    UserDataAccess userDataAccess,
    AuthDataAccess authDataAccess
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

  public AuthData getAuthData(String authToken) throws DataAccessException {
    return authDataAccess.getAuthData(authToken);
  }

  public Collection<UserData> listUsers() throws DataAccessException {
    return userDataAccess.listUsers();
  }

  public AuthData loginUser(LoginRequest loginRequest)
    throws DataAccessException {
    UserData user = userDataAccess.getUser(loginRequest.username());

    if (user == null) {
      throw new DataAccessException("unauthorized");
    }

    if (user.username() == null || user.password() == null) {
      throw new DataAccessException("unauthorized");
    }

    String hashedPassword = user.password();
    if (!BCrypt.checkpw(loginRequest.password(), hashedPassword)) {
      throw new DataAccessException("unauthorized");
    }

    String authToken = authDataAccess.createAuth(user.username());

    return new AuthData(authToken, user.username());
  }

  public void logoutUser(String authToken) throws DataAccessException {
    boolean success = authDataAccess.removeAuth(authToken);
    if (!success) {
      throw new DataAccessException("unauthorized");
    }
  }

  public AuthData registerUser(RegisterRequest registerRequest)
    throws DataAccessException {
    if (
      StringUtils.isEmpty(registerRequest.username()) ||
      StringUtils.isEmpty(registerRequest.password()) ||
      StringUtils.isEmpty(registerRequest.email())
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

    return new AuthData(authToken, user.username());
  }
}
