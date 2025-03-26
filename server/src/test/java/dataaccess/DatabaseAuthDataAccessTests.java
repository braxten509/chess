package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.database.DatabaseAuthDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

  @Test
  void createAuth() throws DataAccessException {
    String authToken = databaseAuthDataAccess.createAuth("newUser");
    assertNotNull(databaseAuthDataAccess.getAuthData(authToken));
  }

  @Test
  void createAuthFail() {
    assertThrows(DataAccessException.class, () -> databaseAuthDataAccess.createAuth("newUser DRP TBLE ();"));
  }

  @Test
  void removeAuth() throws DataAccessException {
    databaseAuthDataAccess.removeAuth(authToken);
    assertNull(databaseAuthDataAccess.getAuthData(authToken));
  }

  @Test
  void removeAuthFail() throws DataAccessException {
    String fakeAuthToken = "askasjydgjdg6576354";
    assertNull(databaseAuthDataAccess.getAuthData(fakeAuthToken));
  }
}
