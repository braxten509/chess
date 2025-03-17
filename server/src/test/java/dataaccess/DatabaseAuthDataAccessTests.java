package dataaccess;
import dataaccess.database.DatabaseAuthDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseAuthDataAccessTests {
    private DatabaseAuthDataAccess databaseAuthDataAccess;
    private String authToken;

    @BeforeEach
    void reset() throws DataAccessException {
        databaseAuthDataAccess = new DatabaseAuthDataAccess();

        databaseAuthDataAccess.clear();

        this.authToken = databaseAuthDataAccess.createAuth("fakeUser");
    }

    @Test
    void clear() throws DataAccessException {
        databaseAuthDataAccess.clear();
        assertNull(databaseAuthDataAccess.getAuthData("fakeUser"));
    }

    @Test
    void getAuthData() throws DataAccessException {
        assertNotNull(databaseAuthDataAccess.getAuthData(authToken));
    }

    @Test
    void getAuthDataFail() throws DataAccessException {
        assertNull(databaseAuthDataAccess.getAuthData("fakefakefakeinvalid"));
    }

}
