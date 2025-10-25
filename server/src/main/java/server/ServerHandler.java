package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryGameDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import java.util.ArrayList;
import java.util.Map;

import io.javalin.http.Context;
import model.*;
import service.GameService;
import service.UserService;
import io.javalin.Javalin;

public class ServerHandler {

  private static final MemoryUserDataAccess USER_DATA_ACCESS =
    new MemoryUserDataAccess();
  private static final MemoryAuthDataAccess AUTH_DATA_ACCESS =
    new MemoryAuthDataAccess();
  private static final MemoryGameDataAccess GAME_DATA_ACCESS =
    new MemoryGameDataAccess();
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

  private static void showErrors(Context ctx, Exception e) {
    if (e.getMessage().equals("unauthorized")) {
      ctx.status(401);
    } else {
      ctx.status(400);
    }
  }

  /**
   * Creates a new account for an unregistered user
   * @return returns an Object, likely a JSON formatted String
   */
  public static void registerUser(Context ctx) {
    try {
      ctx.contentType("application/json");
      RegisterRequest registerRequest = ctx.bodyAsClass(RegisterRequest.class);

      AuthData authData = USER_SERVICE.registerUser(registerRequest);

      ctx.status(200);
      ctx.json(authData);
    } catch (DataAccessException e) {
      if (e.getMessage().equals("already taken")) {
        ctx.status(403);
      } else {
        ctx.status(400);
      }
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    } catch (Exception e) {
      ctx.status(500);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
  }

  /**
   * Logins an existing user
   * @return returns an Object, likely a JSON formatted String
   */
  public static void loginUser(Context ctx) {
    try {
      ctx.contentType("application/json");

      LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);

      AuthData authData = USER_SERVICE.loginUser(loginRequest);
      ctx.status(200);
      ctx.json(authData);
    } catch (DataAccessException e) {
      showErrors(ctx, e);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    } catch (Exception e) {
      ctx.status(500);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
  }

  public static void logoutUser(Context ctx) {
    try {
      ctx.contentType("application/json");

      String authToken = ctx.header("Authorization");

      USER_SERVICE.logoutUser(authToken);
      ctx.status(200);
      ctx.json(new JsonObject());
    } catch (DataAccessException e) {
      ctx.status(401);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    } catch (Exception e) {
      ctx.status(500);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
  }

  public static void createGame(Context ctx) {
    try {
      ctx.contentType("application/json; charset=utf-8");
      String authToken = ctx.header("Authorization");

      JsonObject jsonObject = JsonParser.parseString(
        ctx.body()
      ).getAsJsonObject();
      String gameName = jsonObject.get("gameName").getAsString();

      CreateGameRequest createGameRequest = new CreateGameRequest(
        authToken,
        gameName
      );

      int id = GAME_SERVICE.createGame(createGameRequest);
      ctx.json(Map.of("gameID", id));
    } catch (DataAccessException e) {
      showErrors(ctx, e);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    } catch (Exception e) {
      ctx.status(500);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
  }

  public static void joinGame(Context ctx) {
    try {
      ctx.contentType("application/json");
      String authToken = ctx.header("Authorization");
      JsonObject reqBody = ctx.bodyAsClass(JsonObject.class);

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

      ctx.status(200);
      ctx.json(new JsonObject());
    } catch (DataAccessException e) {
      if (e.getMessage().equals("unauthorized")) {
        ctx.status(401);
      } else if (e.getMessage().equals("already taken")) {
        ctx.status(403);
      } else {
        ctx.status(400);
      }
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    } catch (Exception e) {
      ctx.status(500);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
  }

  public static void listGames(Context ctx) {
    try {
      ctx.contentType("application/json");
      String authToken = ctx.header("Authorization");

      ArrayList<GameData> games = GAME_SERVICE.listGames(authToken);

      ctx.status(200);
      ctx.json(Map.of("games", games));
    } catch (DataAccessException e) {
      ctx.status(401);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    } catch (Exception e) {
      ctx.status(500);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
  }

  /**
   * Clears entire database
   * @return empty JSON string or error
   */
  public static void clearDatabase(Context ctx) {
    try {
      ctx.contentType("application/json");
      USER_SERVICE.clearDataAccess();
      GAME_SERVICE.clearDataAccess();
      ctx.status(200);
      ctx.json(new JsonObject());
    } catch (Exception e) {
      ctx.status(500);
      ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
  }
}
