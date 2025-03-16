package dataaccess;
import dataaccess.database.DatabaseGameDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseGameDataAccessTests {
    private DatabaseGameDataAccess databaseGameDataAccess;
    private int gameID;

    @BeforeEach
    void reset() throws DataAccessException {
        this.databaseGameDataAccess = new DatabaseGameDataAccess();

        databaseGameDataAccess.clear();

        gameID = databaseGameDataAccess.createNewGame("TestGame");
    }

    @Test
    void clear() throws DataAccessException {
        databaseGameDataAccess.clear();

        assertEquals(0, databaseGameDataAccess.listGames().size());
    }

    @Test
    void createNewGame() throws DataAccessException {
        assertEquals(1, databaseGameDataAccess.listGames().size());
    }

    @Test
    void createNewGameFail() {
        assertThrows(DataAccessException.class, () -> {
            databaseGameDataAccess.createNewGame("TestGame);");
        });
    }

    @Test
    void getGame() throws DataAccessException {
        assertNotNull(databaseGameDataAccess.getGame(gameID));
    }

    @Test
    void getGameFail() throws DataAccessException {
        assertNull(databaseGameDataAccess.getGame(gameID + 132));
    }

    @Test
    void joinGame() throws DataAccessException {
        databaseGameDataAccess.joinGame("WHITE", gameID, "fakeUser");
        assertNotEquals(null, databaseGameDataAccess.getGame(gameID).whiteUsername());
    }

    @Test
    void joinGameFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            databaseGameDataAccess.joinGame("WHITE", 0, "fakeUser");
        });
    }

    @Test
    void listGames() throws DataAccessException {
        assertEquals(1, databaseGameDataAccess.listGames().size());
    }
}
