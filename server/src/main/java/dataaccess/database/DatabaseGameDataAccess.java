package dataaccess.database;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDataAccess;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import model.GameData;

public class DatabaseGameDataAccess implements GameDataAccess {

  Gson serializer = new Gson();

  @Override
  public void clear() throws DataAccessException {
    try (var conn = DatabaseManager.getConnection();
        var preparedStatement = conn.prepareStatement("DELETE FROM games")) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("ERROR clearing database: " + e);
    }
  }

  @Override
  public int createNewGame(String gameName) throws DataAccessException {
    if (!gameName.matches("[^);(]+")) {
      throw new DataAccessException("Game name does not match expected syntax");
    }
    try (var conn = DatabaseManager.getConnection();
        var preparedStatement =
            conn.prepareStatement(
                "INSERT INTO games (white_username, black_username, game_name, chess_game) VALUES (?, ?, ?, ?)")) {
      ChessGame newGame = new ChessGame();
      String serializedGame = serializer.toJson(newGame);

      preparedStatement.setString(1, null);
      preparedStatement.setString(2, null);
      preparedStatement.setString(3, gameName);
      preparedStatement.setString(4, serializedGame);
      preparedStatement.executeUpdate();

      try (var preparedStatement2 =
          conn.prepareStatement("SELECT game_id FROM games WHERE game_name = ?")) {
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
    try (var conn = DatabaseManager.getConnection();
        var preparedStatement = conn.prepareStatement("SELECT * FROM games WHERE game_id = ?")) {
      preparedStatement.setInt(1, gameID);
      try (ResultSet result = preparedStatement.executeQuery()) {
        if (result.next()) {
          String whiteUsername = result.getString("white_username");
          String blackUsername = result.getString("black_username");
          String gameName = result.getString("game_name");
          String serializedGame = result.getString("chess_game");

          ChessGame deserializedGame = serializer.fromJson(serializedGame, ChessGame.class);

          return new GameData(gameID, whiteUsername, blackUsername, gameName, deserializedGame);
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
    boolean validID = false;
    for (GameData game : listGames()) {
      if (game.gameID() == gameID) {
        validID = true;
        break;
      }
    }
    if (!validID) {
      throw new DataAccessException("Non-existent game!");
    }
    String sqlString;
    if (Objects.equals(playerColor, "WHITE")) {
      sqlString = "UPDATE games SET white_username = ? WHERE game_id = ?";
    } else {
      sqlString = "UPDATE games SET black_username = ? WHERE game_id = ?";
    }
    if (gameID == 0) {
      throw new DataAccessException("Non-existent game");
    }
    try (var conn = DatabaseManager.getConnection();
        var preparedStatement = conn.prepareStatement(sqlString)) {
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
    try (var conn = DatabaseManager.getConnection();
        var preparedStatement = conn.prepareStatement("SELECT * FROM games")) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          var id = resultSet.getInt("game_id");
          var whiteUsername = resultSet.getString("white_username");
          var blackUsername = resultSet.getString("black_username");
          var gameName = resultSet.getString("game_name");
          String serializedGame = resultSet.getString("chess_game");

          ChessGame deserializedGame = serializer.fromJson(serializedGame, ChessGame.class);

          gamesList.add(new GameData(id, whiteUsername, blackUsername, gameName, deserializedGame));
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("ERROR listing games: " + e);
    }
    return gamesList;
  }
}
