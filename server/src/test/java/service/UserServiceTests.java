package service;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import java.util.ArrayList;
import java.util.List;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserServiceTests {

  private static final UserService USER_SERVICE = new UserService(
    new MemoryUserDataAccess(),
    new MemoryAuthDataAccess()
  );
  private static final UserData USER = new UserData(
    "Jimmethy",
    "abc123",
    "jmail@gmail.com"
  );

  @BeforeEach
  void clear() throws Exception {
    USER_SERVICE.clearDataAccess();
  }

  @Test
  void registerUser() throws DataAccessException {
    var users = USER_SERVICE.listUsers();

    assertEquals(1, users.size());
    assertTrue(users.contains(USER));
  }

  @Test
  void registerUserFail() {
    assertThrows(DataAccessException.class, () -> USER_SERVICE.registerUser(new RegisterRequest("", "a", "a")));
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
    assertThrows(DataAccessException.class, () -> USER_SERVICE.loginUser(
      new LoginRequest(newUser.username(), "wrongPassword")
    ));
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
    assertThrows(DataAccessException.class, () -> USER_SERVICE.logoutUser("fakeToken"));
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
    List<UserData> expected = new ArrayList<>();
    expected.add(new UserData("Jimmethy", "abc123", "jmail@gmail.com"));
    expected.add(new UserData("Jimbo", "abc12asas3", "jmaaaaail@gmail.com"));
    expected.add(
      new UserData("JimmethyJones", "abc1232222", "jmaiLLLl@gmail.com")
    );

    USER_SERVICE.registerUser(
      new RegisterRequest("Jimbo", "abc12asas3", "jmaaaaail@gmail.com")
    );
    USER_SERVICE.registerUser(
      new RegisterRequest("JimmethyJones", "abc1232222", "jmaiLLLl@gmail.com")
    );

    assertTrue(USER_SERVICE.listUsers().containsAll(expected));
  }
}
