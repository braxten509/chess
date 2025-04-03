import static server.ServerCommands.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

import java.util.Arrays;
import java.util.Scanner;
import server.ServerCommands;
import server.ServerFacade;
import ui.EscapeSequences;
import ui.SpacingType;

/**
 * Main class to control the client. Run this to open the client's perspective.
 */
public class Main {

  private static boolean validNumberOfParameters(
      int expectedParameters,
      String userInput
  ) {

    String[] userInputSpliced = userInput.split("\\s+");

    int numberOfGivenParams = 0;
    if (userInputSpliced.length > 1) {
      numberOfGivenParams = Arrays.copyOfRange(userInputSpliced, 1, userInputSpliced.length).length;
    }

    return numberOfGivenParams == expectedParameters;
  }

  private static boolean expectedParameters(
      int expectedNumberOfParams,
      String regex,
      String userInput
  ) {
    if (validNumberOfParameters(expectedNumberOfParams, userInput)) {

      if (regex != null && userInput.matches(regex)) {
        return true;
      }
      if (regex != null && !userInput.matches(regex)) {
        printf("Improper type of parameters. Type 'help' for proper syntax.",
            SpacingType.SURROUND, SET_TEXT_COLOR_YELLOW);
        return false;
      }
      return regex == null;

    } else {

      printf("Improper number of parameters. Expected "
              + expectedNumberOfParams + ". Type 'help' for proper syntax.",
                SpacingType.SURROUND, SET_TEXT_COLOR_YELLOW);
      return false;

    }
  }

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
      String userResponse = scanner.nextLine();

      if (checkForQuit(userResponse)) {
        break;
      }

      var loginRegex = "login\\s+[a-zA-Z0-9]+\\s+[^);]+";
      var registerRegex = "register\\s+[a-zA-Z0-9]+\\s+[^);]+\\s+[a-zA-Z0-9@.&]+";
      var createRegex = "create\\s+[a-zA-Z0-9]{1,20}";
      var joinRegex = "join\\s+[0-9]+\\s+(white|black)";
      var observeRegex = "observe\\s+[0-9]+";

      if (userStatus.equals("LOGGED_OUT")) {
        switch (userResponse.toLowerCase()) {
          case String s when s.matches(
              "\\bhelp\\b.*"
          ) -> {
            if (expectedParameters(0, null, userResponse)) {
              ServerCommands.helpCommand();
            }
          }

          case String s when s.matches(
              "\\blogin\\b.*"
          ) -> {
            if (expectedParameters(2, loginRegex, userResponse)) {
              ServerCommands.loginCommand(serverFacade, userResponse);
            }
          }

          case String s when s.matches(
              "\\bregister\\b.*"
          ) -> {
            if (expectedParameters(3, registerRegex, userResponse)) {
              ServerCommands.registerCommand(serverFacade, userResponse);
            }
          }

          case String s when s.matches(
              "\\bclear\\b.*"
          ) -> {
            if (expectedParameters(0, null, userResponse)) {
              ServerCommands.clearCommand();
            }
          }

          default -> {
            if (
                !userResponse.matches("\\bquit\\b\\s*\\S+")
                    && !userResponse.matches("\\bexit\\b\\s*\\S+")
            ) {
              printf(
                  "Unknown or unavailable command. Type 'help' for a list of valid commands.",
                  SpacingType.UNDER,
                  EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY
              );
            }
          }
        }
      } else {
        switch (userResponse.toLowerCase()) {
          case String s when s.matches(
              "\\bhelp\\b.*"
          ) -> {
            if (expectedParameters(0, null, userResponse)) {
              ServerCommands.helpCommand();
            }
          }

          case String s when s.matches(
              "\\blogout\\b.*"
          ) -> {
            if (expectedParameters(0, null, userResponse)) {
              ServerCommands.logoutCommand(serverFacade);
            }
          }

          case String s when s.matches(
              "\\bcreate\\b.*"
          ) -> {
            if (expectedParameters(1, createRegex, userResponse)) {
              ServerCommands.createCommand(serverFacade, userResponse);
            }
          }

          case String s when s.matches(
              "\\blist\\b.*"
          ) -> {
            if (expectedParameters(0, null, userResponse)) {
              ServerCommands.listCommand(serverFacade);
            }
          }

          case String s when s.matches(
              "\\bjoin\\b.*"
          ) -> {
            if (expectedParameters(2, joinRegex, userResponse)) {
              ServerCommands.joinCommand(serverFacade, userResponse);
            }
          }

          case String s when s.matches(
              "\\bobserve\\b.*"
          ) -> {
            if (expectedParameters(1, observeRegex, userResponse)) {
              int gameId = Integer.parseInt(userResponse.split("\\s")[1]);
              ServerCommands.observeCommand(gameId);
            }
          }

          case String s when s.matches(
              "\\bclear\\b.*"
          ) -> {
            if (expectedParameters(0, null, userResponse)) {
              ServerCommands.clearCommand();
            }
          }

          default -> {
            if (
                !userResponse.matches("\\bquit\\b\\s*\\S+")
                && !userResponse.matches("\\bexit\\b\\s*\\S+")
            ) {
              printf(
                  "Unknown or unavailable command. Type 'help' for a list of valid commands.",
                  SpacingType.UNDER,
                  EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY
              );
            }
          }
        }
      }
    }
  }
}
