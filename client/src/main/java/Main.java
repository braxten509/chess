import client.ChessClient;

/** Main class to control the client. Run this to open the client's perspective. */
public class Main {

  /**
   * Main method for the Main class.
   *
   * @param args using args
   */
  public static void main(String[] args) {
    int serverPort = 8080;
    if (args.length == 1) {
      serverPort = Integer.parseInt(args[0]);
    }

    new ChessClient(serverPort).run();
  }
}
