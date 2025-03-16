package service;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.DataAccessException;
import dataaccess.database.DatabaseAuthDataAccess;
import dataaccess.database.DatabaseUserDataAccess;
import java.util.ArrayList;
import java.util.List;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

class UserServiceTests {

  private static final UserService USER_SERVICE = new UserService(
    new DatabaseUserDataAccess(),
    new DatabaseAuthDataAccess()
  );
  private static final UserData USER = new UserData(
    "Jimmethy",
    "abc123",
    "jmail@gmail.com"
  );

  @BeforeEach
  void clear() throws Exception {
    USER_SERVICE.clearDataAccess();

    var user = new UserData("Jimmethy", "abc123", "jmail@gmail.com");
    USER_SERVICE.registerUser(
      new RegisterRequest(user.username(), user.password(), user.email())
    );
  }

  @Test
  void registerUser() throws DataAccessException {
    var users = USER_SERVICE.listUsers();

    assertEquals(1, users.size());
    assertTrue(users.contains(USER));
  }

  @Test
  void registerUserFail() {
    assertThrows(DataAccessException.class, () ->
      USER_SERVICE.registerUser(new RegisterRequest("", "a", "a"))
    );
  }

  @Test
  void loginUser() throws DataAccessException {
    USER_SERVICE.loginUser(new LoginRequest(USER.username(), USER.password()));
    var usersList = USER_SERVICE.listUsers();

    assertTrue(usersList.contains(USER));
  }

  @Test
  void loginUserFail() throws DataAccessException {
    var newUser = USER_SERVICE.registerUser(
      new RegisterRequest("hello", "a", "b")
    );
    assertThrows(DataAccessException.class, () ->
      USER_SERVICE.loginUser(
        new LoginRequest(newUser.username(), "wrongPassword")
      )
    );
  }

  @Test
  void logoutUser() throws DataAccessException {
    var result = USER_SERVICE.loginUser(
      new LoginRequest(USER.username(), USER.password())
    );
    String authToken = result.authToken();
    USER_SERVICE.logoutUser(authToken);

    var authTokensList = USER_SERVICE.getAuthData(authToken);

    assertNull(authTokensList);
  }

  @Test
  void logoutUserFail() {
    assertThrows(DataAccessException.class, () ->
      USER_SERVICE.logoutUser("fakeToken")
    );
  }

  // test can NEVER fail, so no fail save test was made
  @Test
  void clearDataAccess() throws DataAccessException {
    USER_SERVICE.clearDataAccess();

    assertEquals(0, USER_SERVICE.listUsers().size());
  }

  @Test
  void getUser() throws DataAccessException {
    var foundUser = USER_SERVICE.getUser(USER.username());
    assertEquals(USER, foundUser);
  }

  @Test
  void getUserFail() throws DataAccessException {
    assertNull(USER_SERVICE.getUser("fakeUser"));
  }

  // test can NEVER fail, so no fail save test was made
  @Test
  void listUsers() throws DataAccessException {
    ArrayList<UserData> expected = new ArrayList<>();
    expected.add(new UserData("Jimmethy", BCrypt.hashpw("abc123", BCrypt.gensalt()), "jmail@gmail.com"));
    expected.add(new UserData("Jimbo", BCrypt.hashpw("abc12asas3", BCrypt.gensalt()), "jmaaaaail@gmail.com"));
    expected.add(
      new UserData("JimmethyJones", BCrypt.hashpw("abc1232222", BCrypt.gensalt()), "jmaiLLLl@gmail.com")
    );

    System.out.println(expected);
    System.out.println(USER_SERVICE.listUsers());

    USER_SERVICE.registerUser(
      new RegisterRequest("Jimbo", "abc12asas3", "jmaaaaail@gmail.com")
    );
    USER_SERVICE.registerUser(
      new RegisterRequest("JimmethyJones", "abc1232222", "jmaiLLLl@gmail.com")
    );

    assertTrue(BCrypt.checkpw("abc123", expected.getFirst().password()));
    assertTrue(BCrypt.checkpw("abc12asas3", expected.get(1).password()));
    assertTrue(BCrypt.checkpw("abc1232222", expected.getLast().password()));
  }
}
