import static server.ServerCommands.*;

import java.util.Scanner;
import server.ServerCommands;
import server.ServerFacade;
import ui.EscapeSequences;
import ui.SpacingType;

/**
 * Main class to control the client. Run this to open the client's perspective.
 */
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

    ServerFacade serverFacade = new ServerFacade(serverPort);
    Scanner scanner = new Scanner(System.in);

    printf(
        "♕ Welcome to Chess! Type 'help' to get started ♕",
        SpacingType.UNDER,
        null
    );

    while (true) {
      System.out.print("[" + userStatus + "] >>> ");
      String response = scanner.nextLine();

      if (checkForQuit(response)) {
        break;
      }

      var loginPattern = "login\\s+[a-zA-Z0-9]+\\s+[^);]+";
      var registerPattern = "register\\s+[a-zA-Z0-9]+\\s+[^);]+\\s+[a-zA-Z0-9@.&]+";
      var createPattern = "create\\s+[a-zA-Z0-9]{1,20}";
      var joinPattern = "join\\s+[0-9]+\\s+(white|black)";
      var observePattern = "observe\\s+[0-9]+";

      if (userStatus.equals("LOGGED_OUT")) {
        switch (response.toLowerCase()) {
          case "help" -> ServerCommands.helpCommand();

          case String s when s.matches(
              loginPattern
          ) -> ServerCommands.loginCommand(serverFacade, response);

          case String s when s.matches(
              registerPattern
          ) -> ServerCommands.registerCommand(serverFacade, response);

          case "clear" -> ServerCommands.clearCommand();

          case String s when s.matches(".*register.*") -> printf(
              "Please input entire command.",
              SpacingType.SURROUND,
              EscapeSequences.SET_TEXT_COLOR_YELLOW
          );

          case String s when s.matches(".*login.*") -> printf(
              "Please input entire command.",
              SpacingType.SURROUND,
              EscapeSequences.SET_TEXT_COLOR_YELLOW
          );

          default -> printf(
              "Unknown or unavailable command. Type 'help' for a list of valid commands.",
              SpacingType.UNDER,
              EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY
          );

        }
      } else {
        switch (response.toLowerCase()) {
          case "help" -> ServerCommands.helpCommand();

          case "logout" -> ServerCommands.logoutCommand(serverFacade);

          case String s when s.matches(
                createPattern
          ) -> ServerCommands.createCommand(serverFacade, response);

          case "list" -> ServerCommands.listCommand(serverFacade);

          case String s2 when s2.matches(
                joinPattern
          ) -> ServerCommands.joinCommand(serverFacade, response);

          case String s3 when s3.matches(
                observePattern
          ) -> ServerCommands.observeCommand(
              Integer.parseInt(response.split("\\s")[1])
          );

          case "clear" -> ServerCommands.clearCommand();

          case "create", "join", "observe" -> printf(
              "Please input entire command.",
              SpacingType.SURROUND,
              EscapeSequences.SET_TEXT_COLOR_YELLOW
          );

          default -> printf(
              "Unknown or unavailable command. Type 'help' for a list of valid commands.",
              SpacingType.UNDER,
              EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY
          );
        }
      }
    }
  }
}
