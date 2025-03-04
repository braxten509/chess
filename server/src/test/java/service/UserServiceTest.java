package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static final UserService userService = new UserService(new MemoryUserDataAccess(), new MemoryAuthDataAccess());

    @BeforeEach
    void clear() throws Exception {
        userService.clearDataAccess();
    }

    @Test
    void registerUser() throws DataAccessException {
        var user = new UserData("Jimmethy", "abc123", "jmail@gmail.com");
        userService.registerUser(new RegisterRequest(user.username(), user.password(), user.email()));
        var users = userService.listUsers();

        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

   @Test
   void loginUser() throws DataAccessException {
       var user = new UserData("Jimmethy", "abc123", "jmail@gmail.com");
       userService.registerUser(new RegisterRequest(user.username(), user.password(), user.email()));
   }

    @Test
    void clearDataAccess() throws DataAccessException {
        userService.registerUser(new RegisterRequest("Jimmethy", "abc123", "jmail@gmail.com"));
        userService.registerUser(new RegisterRequest("Jimsim", "abc123aa", "jimsim001@gmail.com"));

        userService.clearDataAccess();

        assertEquals(0, userService.listUsers().size());
    }

    @Test
    void getUser() throws DataAccessException {
        var user = new UserData("Jimmethy", "abc123", "jmail@gmail.com");
        userService.registerUser(new RegisterRequest(user.username(), user.password(), user.email()));

        var foundUser = userService.getUser(user.username());

        assertEquals(user, foundUser);
    }

    @Test
    void listUsers() throws DataAccessException {
        List<UserData> expected = new ArrayList<>();
        expected.add(new UserData("Jimmethy", "abc123", "jmail@gmail.com"));
        expected.add(new UserData("Jimbo", "abc12asas3", "jmaaaaail@gmail.com"));
        expected.add(new UserData("JimmethyJones", "abc1232222", "jmaiLLLl@gmail.com"));

        userService.registerUser(new RegisterRequest("Jimmethy", "abc123", "jmail@gmail.com"));
        userService.registerUser(new RegisterRequest("Jimbo", "abc12asas3", "jmaaaaail@gmail.com"));
        userService.registerUser(new RegisterRequest("JimmethyJones", "abc1232222", "jmaiLLLl@gmail.com"));

        assertTrue(userService.listUsers().containsAll(expected));
    }
}