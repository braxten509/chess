package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import java.util.Map;

import model.LoginRequest;
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

  /**
   * turns a JSON string into an object
   * @param req request object
   * @param res response object
   * @param classOfT type of object (Object.class)
   * @return returns object 'T', whatever it is defined as
   * @param <T> labels as a generic method
   */
  private static <T> T turnIntoObject(
    Request req,
    Response res,
    Class<T> classOfT
  ) {
    return new Gson().fromJson(req.body(), classOfT);
  }

  /**
   * @param object the input (usually an Object or String)
   * @return "param1"
   */
  private static <T> String turnIntoJson(T object) {
    return new Gson().toJson(object);
  }

  /**
   * @param keyword what the keyword is
   * @param definition what the second parameter is
   * @return {"param1", "definition of param1 [param2]"}
   */
  @SuppressWarnings("SameParameterValue")
  private static <T> String turnIntoJson(String keyword, T definition) {
    return new Gson().toJson(Map.of(keyword, definition));
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

      RegisterRequest registerRequest = turnIntoObject(req, res, RegisterRequest.class);

      if (
        registerRequest.username() == null ||
        registerRequest.password() == null ||
        registerRequest.email() == null
      ) {
        res.status(400);
        return turnIntoJson("message", "Error: bad request");
      }

      var successResult = userService.register(registerRequest);
      res.status(200);
      return turnIntoJson(successResult);

    } catch (DataAccessException e) {
      res.status(403);
      return turnIntoJson("message", "Error: already taken");

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
    res.type("application/json");

    LoginRequest loginRequest = turnIntoObject(req, res, LoginRequest.class);

    //    try {
    //
    //    } catch (DataAccessException e) {
    //
    //    }

    return new Object();
  }

  /**
   * Clears entire database
   * @param req request object
   * @param res response object
   * @return empty JSON string or error
   */
  public static Object clearDatabase(Request req, Response res) {
    try {
      res.type("application/json");
      userService.clearDataAccess();
      res.status(200);
      return turnIntoJson(Map.of());

    } catch (Exception e) {
      res.status(500);
      turnIntoJson("message", "Error: " + e.getMessage());
      return turnIntoJson("message", "Error: " + e.getMessage());
    }
  }
}
