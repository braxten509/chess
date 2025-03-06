package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.AuthData;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {
    private static final UserService userService = new UserService(new MemoryUserDataAccess(), new MemoryAuthDataAccess());
    private static final UserData user = new UserData("Jimmethy", "abc123", "jmail@gmail.com");

    @BeforeEach
    void clear() throws Exception {
        userService.clearDataAccess();

        var user = new UserData("Jimmethy", "abc123", "jmail@gmail.com");
        AuthData authData = userService.registerUser(new RegisterRequest(user.username(), user.password(), user.email()));
    }

    @Test
    void registerUser() throws DataAccessException {
        var users = userService.listUsers();

        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

    @Test
    void registerUserFail() {
        assertThrows(DataAccessException.class, () -> {
            userService.registerUser(new RegisterRequest("", "a", "a"));
        });
    }

   @Test
   void loginUser() throws DataAccessException {
        userService.loginUser(new LoginRequest(user.username(), user.password()));
        var usersList = userService.listUsers();

        assertTrue(usersList.contains(user));
   }

    @Test
    void loginUserFail() throws DataAccessException {
        var newUser = userService.registerUser(new RegisterRequest("hello", "a", "b"));
        assertThrows(DataAccessException.class, () -> {
            userService.loginUser(new LoginRequest(newUser.username(), "wrongPassword"));
        });
    }

    @Test
    void logoutUser() throws DataAccessException {
        var result = userService.loginUser(new LoginRequest(user.username(), user.password()));
        String authToken = result.authToken();
        userService.logoutUser(authToken);

        var authTokensList = userService.getAuthData(authToken);

        assertNull(authTokensList);
    }

    @Test
    void logoutUserFail() {
        assertThrows(DataAccessException.class, () -> {
            userService.logoutUser("fakeToken");
        });
    }

    // test can NEVER fail, so no fail save test was made
    @Test
    void clearDataAccess() throws DataAccessException {
        userService.clearDataAccess();

        assertEquals(0, userService.listUsers().size());
    }

    @Test
    void getUser() throws DataAccessException {
        var foundUser = userService.getUser(user.username());
        assertEquals(user, foundUser);
    }

    @Test
    void getUserFail() throws DataAccessException {
        assertNull(userService.getUser("fakeUser"));
    }

    // test can NEVER fail, so no fail save test was made
    @Test
    void listUsers() throws DataAccessException {
        List<UserData> expected = new ArrayList<>();
        expected.add(new UserData("Jimmethy", "abc123", "jmail@gmail.com"));
        expected.add(new UserData("Jimbo", "abc12asas3", "jmaaaaail@gmail.com"));
        expected.add(new UserData("JimmethyJones", "abc1232222", "jmaiLLLl@gmail.com"));

        userService.registerUser(new RegisterRequest("Jimbo", "abc12asas3", "jmaaaaail@gmail.com"));
        userService.registerUser(new RegisterRequest("JimmethyJones", "abc1232222", "jmaiLLLl@gmail.com"));

        assertTrue(userService.listUsers().containsAll(expected));
    }
}