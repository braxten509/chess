package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDataAccess;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import model.UserData;

public class DatabaseUserDataAccess implements UserDataAccess {

  @Override
  public UserData getUser(String username) throws DataAccessException {
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement(
        "SELECT * FROM users WHERE username = ?"
      )
    ) {
      preparedStatement.setString(1, username);

      try (ResultSet result = preparedStatement.executeQuery()) {
        if (result.next()) {
          String foundUsername = result.getString("username");
          String foundPassword = result.getString("password");
          String foundEmail = result.getString("email");
          return new UserData(foundUsername, foundPassword, foundEmail);
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(
        "ERROR getting user " + username + ": " + e
      );
    }
    return null;
  }

  @Override
  public UserData createUser(String username, String password, String email)
    throws DataAccessException {
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement(
        "INSERT INTO users (username, password, email) VALUES (?, ?, ?)"
      )
    ) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      preparedStatement.setString(3, email);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("ERROR creating user: " + e);
    }
    return getUser(username);
  }

  @Override
  public void clear() throws DataAccessException {
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement("DELETE FROM users")
    ) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("ERROR clearing database: " + e);
    }
  }

  @Override
  public Collection<UserData> listUsers() throws DataAccessException {
    ArrayList<UserData> userList = new ArrayList<>();
    try (
      var conn = DatabaseManager.getConnection();
      var preparedStatement = conn.prepareStatement("SELECT * FROM users")
    ) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          var username = resultSet.getString("username");
          var password = resultSet.getString("password");
          var email = resultSet.getString("email");

          userList.add(new UserData(username, password, email));
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("ERROR listing users: " + e);
    }
    return userList;
  }
}
