package server;

import io.javalin.Javalin;

public class Server {
  private final Javalin javalin;

  public Server() {
    javalin = Javalin.create(javalinConfig -> javalinConfig.staticFiles.add("web"))
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
