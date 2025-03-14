package dataaccess.database;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDataAccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import model.GameData;
import model.UserData;

public class DatabaseGameDataAccess implements GameDataAccess {

  @Override
  public void clear() throws DataAccessException {
    try (
            var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement("DELETE FROM games")
    ) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("ERROR clearing database: " + e);
    }
  }

  @Override
  public int createNewGame(String gameName) throws DataAccessException {
    try (
            var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement(
                    "INSERT INTO games (white_username, black_username, game_name, game) VALUES (?, ?, ?, ?)"
            )
    ) {
      /* ADD CHESS GAME */
      preparedStatement.setString(1, null);
      preparedStatement.setString(2, null);
      preparedStatement.setString(3, gameName);
      preparedStatement.setString(4, "CHESS GAME");
      preparedStatement.executeUpdate();

      try (var preparedStatement2 = conn.prepareStatement("SELECT game_id FROM games WHERE game_name = ?")) {
        preparedStatement2.setString(1, gameName);
        try (ResultSet resultSet = preparedStatement2.executeQuery()) {
          if (resultSet.next()) {
            return resultSet.getInt("game_id");
          }
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("ERROR creating user: " + e);
    }
    return 0;
  }

  @Override
  public GameData getGame(int gameID) throws DataAccessException {
    try (
            var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement("SELECT * FROM games WHERE game_id = ?")
    ) {
      preparedStatement.setInt(1, gameID);
      try (ResultSet result = preparedStatement.executeQuery()) {
        if (result.next()) {
          String whiteUsername = result.getString("white_username");
          String blackUsername = result.getString("black_username");
          String gameName = result.getString("game_name");
          String game = result.getString("game");

          /* TODO: ADD CHESS GAME */
          return new GameData(gameID, whiteUsername, blackUsername, gameName, new ChessGame());
        }
        return null;
      }
    } catch (SQLException e) {
      throw new DataAccessException("ERROR getting game " + gameID + ": " + e);
    }
  }

  @Override
  public void joinGame(String playerColor, int gameID, String playerUsername)
    throws DataAccessException {
    String SQLString = "";
    if (Objects.equals(playerColor, "WHITE")) {
      SQLString = "UPDATE games SET white_username = ? WHERE game_id = ?";
    } else {
      SQLString = "UPDATE games SET black_username = ? WHERE game_id = ?";
    }
    try (
            var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement(SQLString)
    ) {
      preparedStatement.setString(1, playerUsername);
      preparedStatement.setInt(2, gameID);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("ERROR joining game " + gameID + ": " + e);
    }
  }

  @Override
  public ArrayList<GameData> listGames() throws DataAccessException {
    ArrayList<GameData> gamesList = new ArrayList<>();
    try (
            var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement("SELECT * FROM games")
    ) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          var id = resultSet.getInt("game_id");
          var whiteUsername = resultSet.getString("white_username");
          var blackUsername = resultSet.getString("black_username");
          var gameName = resultSet.getString("game_name");
          var game = resultSet.getString("game");

          /* TODO: Add Chess Game */
          gamesList.add(new GameData(id, whiteUsername, blackUsername, gameName, new ChessGame()));
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("ERROR listing games: " + e);
    }
    return gamesList;
  }
}
