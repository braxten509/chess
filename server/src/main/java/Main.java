import chess.*;
import server.Server;

public class Main {

  public static void main(String[] args) {

    System.out.println("♕ 240 Chess Server");

    var server = new Server();

    server.run(8080);
  }
}
