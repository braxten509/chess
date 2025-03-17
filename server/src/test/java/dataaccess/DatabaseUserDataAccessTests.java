package dataaccess;
import dataaccess.database.DatabaseUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseUserDataAccessTests {
    private DatabaseUserDataAccess databaseUserDataAccess;
    private final UserData user = new UserData("testUser", "testPassword", "test@email.com");

    @BeforeEach
    void reset() throws DataAccessException {
        this.databaseUserDataAccess = new DatabaseUserDataAccess();

        databaseUserDataAccess.clear();

        databaseUserDataAccess.createUser(user.username(), user.password(), user.email());
    }

    @Test
    void clear() throws DataAccessException {
        databaseUserDataAccess.clear();

        assertEquals(0, databaseUserDataAccess.listUsers().size());
    }

    @Test
    void createUser() throws DataAccessException {
        assertEquals(1, databaseUserDataAccess.listUsers().size());
    }

    @Test
    void createUserFail() {
        assertThrows(DataAccessException.class, () -> {
            databaseUserDataAccess.createUser("USERNAME);", "))));;;", "valid@gmail.com");
        });
    }

    @Test
    void getUser() throws DataAccessException {
        assertNotNull(databaseUserDataAccess.getUser("testUser"));
    }

    @Test
    void getUserFail() throws DataAccessException {
        assertNull(databaseUserDataAccess.getUser("nonexistantUser"));
    }

    /* test can NEVER fail, so no fail save test was made */
    @Test
    void listUsers() throws DataAccessException {
        databaseUserDataAccess.createUser("testUser2", "hellothere", "valid@gmail.com");

        assertEquals(2, databaseUserDataAccess.listUsers().size());
    }
}
