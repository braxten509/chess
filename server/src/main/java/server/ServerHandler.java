package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDataAccess;
import dataaccess.memory.MemoryUserDataAccess;
import model.RegisterRequest;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ServerHandler {

    private static final MemoryUserDataAccess userDataAccess = new MemoryUserDataAccess();
    private static final MemoryAuthDataAccess authDataAccess = new MemoryAuthDataAccess();
    private static final UserService userService = new UserService(userDataAccess, authDataAccess);

    public static Object registerUser(Request req, Response res) {
        try {
            res.type("application/json");

            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);

            if (registerRequest.username().isEmpty() || registerRequest.password().isEmpty() || registerRequest.email().isEmpty()) {
                res.status(400);
                return new Gson().toJson("Field cannot be left blank");
            }

            var result = userService.register(registerRequest);

            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error accessing data: " + e.getMessage()));
        }
    }
}
