package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
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
        userService.registerUser(new RegisterRequest(user.username(), user.password(), user.email()));
        // var authToken = userService.getAuthData(user.username()).authToken();
    }

    @Test
    @Order(1)
    void registerUser() throws DataAccessException {
        var users = userService.listUsers();

        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

   @Test
   @Order(2)
   void loginUser() throws DataAccessException {

   }

    @Test
    @Order(3)
    void clearDataAccess() throws DataAccessException {
        userService.clearDataAccess();

        assertEquals(0, userService.listUsers().size());
    }

    @Test
    @Order(4)
    void getUser() throws DataAccessException {
        var foundUser = userService.getUser(user.username());
        assertEquals(user, foundUser);
    }

    @Test
    @Order(5)
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