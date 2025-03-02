package server;

import spark.*;

public class Server {

    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", ServerHandler::registerUser);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();

        System.out.println("Success!");

        return Spark.port();
    }



    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
