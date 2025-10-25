package server;

import io.javalin.Javalin;

public class Server {

  public Server() {}

  public void run(int desiredPort) {
    Javalin.create(javalinConfig -> javalinConfig.staticFiles.add("web"))
            .post("/user", ServerHandler::registerUser)
            .post("/session", ServerHandler::loginUser)
            .post("/game", ServerHandler::createGame)
            .put("/game", ServerHandler::joinGame)
            .get("/game", ServerHandler::listGames)
            .delete("/session", ServerHandler::logoutUser)
            .delete("/db", ServerHandler::clearDatabase)
            .start(desiredPort);
  }

//  public int run(int desiredPort) {
//    Spark.port(desiredPort);
//
//    Spark.staticFiles.location("web");
//
//    // Register your endpoints and handle exceptions here.
//    Spark.post("/user", ServerHandler::registerUser);
//    Spark.post("/session", ServerHandler::loginUser);
//    Spark.post("/game", ServerHandler::createGame);
//    Spark.put("/game", ServerHandler::joinGame);
//    Spark.get("/game", ServerHandler::listGames);
//    Spark.delete("/session", ServerHandler::logoutUser);
//    Spark.delete("/db", ServerHandler::clearDatabase);
//
//    //This line initializes the server and can be removed once you have a functioning endpoint
//    Spark.init();
//
//    Spark.awaitInitialization();
//
//    System.out.println("Success!");
//
//    return Spark.port();
//  }

//  public void stop() {
//    Spark.stop();
//    Spark.awaitStop();
//  }
}
