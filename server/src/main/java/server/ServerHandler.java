package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import java.util.Map;
import model.RegisterRequest;
import service.UserService;
import spark.Request;
import spark.Response;

public class ServerHandler {

  private static final MemoryUserDataAccess userDataAccess =
    new MemoryUserDataAccess();
  private static final MemoryAuthDataAccess authDataAccess =
    new MemoryAuthDataAccess();
  private static final UserService userService = new UserService(
    userDataAccess,
    authDataAccess
  );

  public static Object registerUser(Request req, Response res) {
    try {
      res.type("application/json");

      RegisterRequest registerRequest = new Gson()
        .fromJson(req.body(), RegisterRequest.class);

      if (
        registerRequest.username() == null ||
        registerRequest.password() == null ||
        registerRequest.email() == null
      ) {
        res.status(400);
        return new Gson().toJson(Map.of("message", "Error: bad request"));
      }

      var result = userService.register(registerRequest);

      res.status(200);
      return new Gson().toJson(result);
    } catch (DataAccessException e) {
      res.status(403);
      return new Gson().toJson(Map.of("message", "Error: already taken"));
    } catch (Exception e) {
      res.status(500);
      return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
    }
  }

  public static Object clearDatabase(Request req, Response res) {
    try {
      res.type("application/json");
      userService.clearDataAccess();
      res.status(200);
      return new JsonObject().toString();
    } catch (Exception e) {
      res.status(500);
      return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
    }
  }
}
