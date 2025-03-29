import static server.ServerCommands.*;

import java.util.Scanner;

import server.ServerFacade;
import server.ServerCommands;
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
          default -> printf("Unknown or unavailable command. Type 'help' for a list of valid commands.", SpacingType.UNDER, EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        }
      } else {
        switch (response.toLowerCase()) {
          case "help" -> ServerCommands.helpCommand();
          case "logout" -> ServerCommands.logoutCommand(serverFacade);
          case String s when s.matches("create\\s+[a-zA-Z0-9]{1,20}") -> ServerCommands.createCommand(serverFacade, response);
          case "list" -> ServerCommands.listCommand(serverFacade);
          case String s when s.matches("join\\s+[0-9]+\\s+[a-zA-Z0-9]+") -> ServerCommands.joinCommand(serverFacade, response);
          case "clear" -> ServerCommands.clearCommand();
          default -> printf("Unknown or unavailable command. Type 'help' for a list of valid commands.", SpacingType.UNDER, EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        }
      }

      if (checkForQuit(response)) {
        break;
      }
    }
  }
}
