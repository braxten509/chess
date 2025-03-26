package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import dataaccess.database.DatabaseAuthDataAccess;
import dataaccess.database.DatabaseGameDataAccess;
import dataaccess.database.DatabaseUserDataAccess;
import java.util.ArrayList;
import java.util.Map;
import model.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ServerHandler {

  private static final DatabaseUserDataAccess USER_DATA_ACCESS =
    new DatabaseUserDataAccess();
  private static final DatabaseAuthDataAccess AUTH_DATA_ACCESS =
    new DatabaseAuthDataAccess();
  private static final DatabaseGameDataAccess GAME_DATA_ACCESS =
    new DatabaseGameDataAccess();
  private static final Gson GSON = new GsonBuilder()
    .disableHtmlEscaping()
    .create();

  private static final GameService GAME_SERVICE = new GameService(
    GAME_DATA_ACCESS,
    AUTH_DATA_ACCESS
  );
  private static final UserService USER_SERVICE = new UserService(
    USER_DATA_ACCESS,
    AUTH_DATA_ACCESS
  );

  private static void showErrors(Response res, Exception e) {
    if (e.getMessage().equals("unauthorized")) {
      res.status(401);
    } else {
      res.status(400);
    }
  }

  /**
   * turns a JSON string into an object
   * @param req request object
   * @param classOfT type of object (Object.class)
   * @return returns object 'T', whatever it is defined as
   * @param <T> labels as a generic method
   */
  private static <T> T turnIntoObject(Request req, Class<T> classOfT) {
    return GSON.fromJson(req.body(), classOfT);
  }

  /**
   * @param object the input (usually an Object or String)
   * @return "param1"
   */
  private static <T> String turnIntoJson(T object) {
    return GSON.toJson(object);
  }

  /**
   * @param keyword what the keyword is
   * @param definition what the second parameter is
   * @return {"param1", "definition of param1 [param2]"}
   */
  @SuppressWarnings("SameParameterValue")
  private static <T> String turnIntoJson(String keyword, T definition) {
    return GSON.toJson(Map.of(keyword, definition));
  }

  /**
   * Creates a new account for an unregistered user
   * @param req request object
   * @param res response object
   * @return returns an Object, likely a JSON formatted String
   */
  public static Object registerUser(Request req, Response res) {
    try {
      res.type("application/json");
      RegisterRequest registerRequest = turnIntoObject(
        req,
        RegisterRequest.class
      );

      AuthData authData = USER_SERVICE.registerUser(registerRequest);

      res.status(200);
      return turnIntoJson(authData);
    } catch (DataAccessException e) {
      if (e.getMessage().equals("already taken")) {
        res.status(403);
      } else {
        res.status(400);
      }
      return turnIntoJson("message", "Error: " + e.getMessage());
    } catch (Exception e) {
      res.status(500);
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }

  /**
   * Logins an existing user
   * @param req request object
   * @param res response object
   * @return returns an Object, likely a JSON formatted String
   */
  public static Object loginUser(Request req, Response res) {
    try {
      res.type("application/json");

      LoginRequest loginRequest = turnIntoObject(req, LoginRequest.class);

      AuthData authData = USER_SERVICE.loginUser(loginRequest);
      res.status(200);
      return turnIntoJson(authData);
    } catch (DataAccessException e) {
      showErrors(res, e);
      return turnIntoJson("message", "Error: " + e.getMessage());
    } catch (Exception e) {
      res.status(500);
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }

  public static Object logoutUser(Request req, Response res) {
    try {
      res.type("application/json");

      String authToken = req.headers("Authorization");

      USER_SERVICE.logoutUser(authToken);
      res.status(200);
      return turnIntoJson(new JsonObject());
    } catch (DataAccessException e) {
      res.status(401);
      return turnIntoJson("message", "Error: " + e.getMessage());
    } catch (Exception e) {
      res.status(500);
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }

  public static Object createGame(Request req, Response res) {
    try {
      res.type("application/json; charset=utf-8");
      String authToken = req.headers("Authorization");

      JsonObject jsonObject = JsonParser.parseString(
        req.body()
      ).getAsJsonObject();
      String gameName = jsonObject.get("gameName").getAsString();

      CreateGameRequest createGameRequest = new CreateGameRequest(
        authToken,
        gameName
      );

      int gameId = GAME_SERVICE.createGame(createGameRequest);
      return turnIntoJson(new CreateGameResult(gameId));
    } catch (DataAccessException e) {
      showErrors(res, e);
      return turnIntoJson("message", "Error: " + e.getMessage());
    } catch (Exception e) {
      res.status(500);
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }

  public static Object joinGame(Request req, Response res) {
    try {
      res.type("application/json");
      String authToken = req.headers("Authorization");
      JsonObject reqBody = turnIntoObject(req, JsonObject.class);

      if (
        reqBody == null ||
        reqBody.get("playerColor") == null ||
        reqBody.get("gameID") == null
      ) {
        throw new DataAccessException("bad request");
      }

      JoinGameRequest joinGameRequest = new JoinGameRequest(
        authToken,
        reqBody.get("playerColor").getAsString(),
        reqBody.get("gameID").getAsInt()
      );

      GAME_SERVICE.joinGame(joinGameRequest);

      res.status(200);
      return turnIntoJson(new JsonObject());
    } catch (DataAccessException e) {
      if (e.getMessage().equals("unauthorized")) {
        res.status(401);
      } else if (e.getMessage().equals("already taken")) {
        res.status(403);
      } else {
        res.status(400);
      }
      return turnIntoJson("message", "Error: " + e.getMessage());
    } catch (Exception e) {
      res.status(500);
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }

  public static Object listGames(Request req, Response res) {
    try {
      res.type("application/json");
      String authToken = req.headers("Authorization");

      ArrayList<GameData> games = GAME_SERVICE.listGames(authToken);

      res.status(200);
      return turnIntoJson(new ListGamesResult(games));
    } catch (DataAccessException e) {
      res.status(401);
      return turnIntoJson("message", "Error: " + e.getMessage());
    } catch (Exception e) {
      res.status(500);
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }

  /**
   * Clears entire database
   * @param ignoredReq request object
   * @param res response object
   * @return empty JSON string or error
   */
  public static Object clearDatabase(Request ignoredReq, Response res) {
    try {
      res.type("application/json");
      USER_SERVICE.clearDataAccess();
      GAME_SERVICE.clearDataAccess();
      res.status(200);
      return turnIntoJson(new JsonObject());
    } catch (Exception e) {
      res.status(500);
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }
}
