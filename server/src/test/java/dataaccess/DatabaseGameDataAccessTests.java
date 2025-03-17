package dataaccess;

import dataaccess.database.DatabaseUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseGameDataAccessTests {
    private DatabaseUserDataAccess databaseUserDataAccess;
    private final UserData userData = new UserData("testusername", "testPassword", "testEmail@email.com");

    @BeforeEach
    void reset() throws DataAccessException {
        this.databaseUserDataAccess = new DatabaseUserDataAccess();

        databaseUserDataAccess.clear();

        databaseUserDataAccess.createUser(userData.username(), userData.password(), userData.email());
    }

    @Test
    void clear() throws DataAccessException {
        databaseUserDataAccess.clear();

        assertEquals(0, databaseUserDataAccess.listUsers().size());
    }

}
