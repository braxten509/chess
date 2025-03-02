package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserServiceTest {
    static final UserService userService = new UserService(new MemoryUserDataAccess(), new MemoryAuthDataAccess());

    @BeforeEach
    void clear() throws DataAccessException {
        userService.clearDataAccess();
    }

    @Test
    void register() throws DataAccessException {
        userService.register(new RegisterRequest("Jimmethy", "abc123", "jmail@gmail.com"));
    }

    @Test
    void clearDataAccess() throws DataAccessException {
        userService.register(new RegisterRequest("Jimmethy", "abc123", "jmail@gmail.com"));

        userService.clearDataAccess();
    }
}