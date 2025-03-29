import static server.ServerCommands.*;

import java.util.Scanner;
import server.ServerCommands;
import server.ServerFacade;
import ui.EscapeSequences;
import ui.SpacingType;

public class Main {

  public static void main(String[] args) {
    var serverUrl = "http://localhost:8080";
    if (args.length == 1) {
      serverUrl = args[0];
    }

    ServerFacade serverFacade = new ServerFacade(serverUrl);
    Scanner scanner = new Scanner(System.in);

    printf(
      "♕ Welcome to Chess! Type 'help' to get started ♕",
      SpacingType.UNDER,
      null
    );

    while (true) {
      System.out.print("[" + userStatus + "] >>> ");
      String response = scanner.nextLine();

      if (userStatus.equals("LOGGED_OUT")) {
        switch (response.toLowerCase()) {
          case "help" -> ServerCommands.helpCommand();
          case "login" -> ServerCommands.loginCommand(serverFacade);
          case "register" -> ServerCommands.registerCommand(serverFacade);
          case "clear" -> ServerCommands.clearCommand();
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
            "create\\s+[a-zA-Z0-9]{1,20}"
          ) -> ServerCommands.createCommand(serverFacade, response);
          case "list" -> ServerCommands.listCommand(serverFacade);
          case String s2 when s2.matches(
            "join\\s+[0-9]+\\s+(white|black)"
          ) -> ServerCommands.joinCommand(serverFacade, response);
          case String s3 when s3.matches("observe\\s+[0-9]+") -> ServerCommands.observeCommand(serverFacade);
          case "clear" -> ServerCommands.clearCommand();
          case "create", "join", "observe" -> printf("Please input entire command.", SpacingType.SURROUND, EscapeSequences.SET_TEXT_COLOR_YELLOW);
          default -> printf(
            "Unknown or unavailable command. Type 'help' for a list of valid commands.",
            SpacingType.UNDER,
            EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY
          );
        }
      }

      if (checkForQuit(response)) {
        break;
      }
    }
  }
}
