package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;

public class Server {
  private final Javalin javalin;

  public Server() {

    javalin = Javalin.create(
            javalinConfig -> {
              javalinConfig.staticFiles.add("web");
              javalinConfig.jsonMapper(new JavalinGson());
            }
            )
            .post("/user", ServerHandler::registerUser)
            .post("/session", ServerHandler::loginUser)
            .post("/game", ServerHandler::createGame)
            .put("/game", ServerHandler::joinGame)
            .get("/game", ServerHandler::listGames)
            .delete("/session", ServerHandler::logoutUser)
            .delete("/db", ServerHandler::clearDatabase);
  }

  public int run(int port) {
    javalin.start(port);

    System.out.println("Server started on port: " + javalin.port());

    return javalin.port();
  }

  public void stop() {
    javalin.stop();
  }
}
