package dataaccess.database;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import model.AuthData;

public class DatabaseAuthDataAccess implements AuthDataAccess {

  private static String generateToken() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String createAuth(String username) throws DataAccessException {
    String authToken = generateToken();
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement(
        "INSERT INTO auths (auth_token, username) VALUES (?, ?)"
      )
    ) {
      preparedStatement.setString(1, authToken);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("ERROR creating auth: " + e);
    }
    return authToken;
  }

  @Override
  public boolean removeAuth(String authToken) throws DataAccessException {
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement(
        "DELETE FROM auths WHERE auth_token = ?"
      )
    ) {
      preparedStatement.setString(1, authToken);
      int rowsDeleted = preparedStatement.executeUpdate();
      return rowsDeleted > 0;
    } catch (SQLException e) {
      throw new DataAccessException("ERROR removing auth: " + e);
    }
  }

  @Override
  public void clear() throws DataAccessException {
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement("DELETE FROM auths")
    ) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("ERROR clearing database: " + e);
    }
  }

  @Override
  public AuthData getAuthData(String authToken) throws DataAccessException {
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement(
        "SELECT * FROM auths WHERE auth_token = ?"
      )
    ) {
      preparedStatement.setString(1, authToken);

      try (ResultSet result = preparedStatement.executeQuery()) {
        if (result.next()) {
          String foundAuthToken = result.getString("auth_token");
          String foundUsername = result.getString("username");
          return new AuthData(foundAuthToken, foundUsername);
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("ERROR getting authData: " + e);
    }
    return null;
  }
}
