package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {

  private static String databaseName;
  private static String user;
  private static String password;
  private static String connectionUrl;

  /*
   * Load the database information for the db.properties file.
   */
  static {
    loadPropertiesFromResources();
  }

  /** Loads the properties from a test or real source for the database. */
  public static void loadProperties(Properties props) {
    databaseName = props.getProperty("db.name");
    user = props.getProperty("db.user");
    password = props.getProperty("db.password");

    var host = props.getProperty("db.host");
    var port = Integer.parseInt(props.getProperty("db.port"));
    connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
  }

  /** Loads the properties from a file. */
  public static void loadPropertiesFromResources() {
    try {
      try (var propStream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
        if (propStream == null) {
          throw new Exception("Unable to load db.properties");
        }
        Properties props = new Properties();
        props.load(propStream);
        loadProperties(props);
      }
    } catch (Exception ex) {
      throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
    }
  }

  /** Creates the database if it does not already exist. */
  public static void createDatabase() throws DataAccessException {
    try {
      var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
      var conn = DriverManager.getConnection(connectionUrl, user, password);
      try (var preparedStatement = conn.prepareStatement(statement)) {
        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      throw new DataAccessException("ERROR: " + e.getMessage());
    }
  }

  /** Creates the tables if they do not already exist. */
  public static void createTables() throws DataAccessException {
    try (var conn = getConnection();
        Statement statement = conn.createStatement()) {
      String command =
          """
          CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(255) NOT NULL PRIMARY KEY,
            hashed_password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL
          );
          """;
      statement.executeUpdate(command);

      command =
          """
        CREATE TABLE IF NOT EXISTS games (
          game_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
          white_username VARCHAR(255),
          black_username VARCHAR(255),
          game_name VARCHAR(255) NOT NULL,
          chess_game TEXT NOT NULL
        );
        """;
      statement.executeUpdate(command);

      command =
          """
        CREATE TABLE IF NOT EXISTS auths (
          auth_token VARCHAR(255) NOT NULL PRIMARY KEY,
          username VARCHAR(255) NOT NULL
        );
        """;
      statement.executeUpdate(command);
    } catch (SQLException e) {
      System.out.println("ERROR: " + e);
    }
  }

  /**
   * Create a connection to the database and sets the catalog based upon the properties specified in
   * db.properties. Connections to the database should be short-lived, and you must close the
   * connection when you are done with it. The easiest way to do that is with a try-with-resource
   * block. <code>
   * try (var conn = DbInfo.getConnection(databaseName)) {
   * // execute SQL statements.
   * }
   * </code>
   */
  public static Connection getConnection() throws DataAccessException {
    try {
      var conn = DriverManager.getConnection(connectionUrl, user, password);
      conn.setCatalog(databaseName);
      return conn;
    } catch (SQLException e) {
      throw new DataAccessException("ERROR: " + e.getMessage());
    }
  }
}
