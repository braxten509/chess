package client;

import client.formatting.EscapeSequences;
import client.formatting.SpacingType;

import java.util.Arrays;

import static client.ChessClient.printf;
import static client.ChessClient.userStatus;
import static client.UserCommands.*;
import static client.formatting.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class RequestProcessor {

  private final ServerFacade serverFacade;

  public RequestProcessor(ServerFacade serverFacade) {
    this.serverFacade = serverFacade;
  }

  private boolean validNumberOfParameters(
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

  private boolean expectedParameters(
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

  private void loggedOutCommands(String command) {
    var loginRegex = "login\\s+[a-zA-Z0-9]+\\s+[^);]+";
    var registerRegex = "register\\s+[a-zA-Z0-9]+\\s+[^);]+\\s+[a-zA-Z0-9@.&]+";

    switch (command.toLowerCase()) {
      case String s when s.matches(
          "\\bhelp\\b.*"
      ) -> {
        if (expectedParameters(0, null, command)) {
          helpCommand();
        }
      }

      case String s when s.matches(
          "\\blogin\\b.*"
      ) -> {
        if (expectedParameters(2, loginRegex, command)) {
          loginCommand(serverFacade, command);
        }
      }

      case String s when s.matches(
          "\\bregister\\b.*"
      ) -> {
        if (expectedParameters(3, registerRegex, command)) {
          registerCommand(serverFacade, command);
        }
      }

      case String s when s.matches(
          "\\bclear\\b.*"
      ) -> {
        if (expectedParameters(0, null, command)) {
          clearCommand();
        }
      }

      default -> {
        if (
            !command.matches("\\bquit\\b\\s*\\S+")
                && !command.matches("\\bexit\\b\\s*\\S+")
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

  private void loggedInCommands(String command) {
    var createRegex = "create\\s+[a-zA-Z0-9]{1,20}";
    var joinRegex = "join\\s+[0-9]+\\s+(white|black)";
    var observeRegex = "observe\\s+[0-9]+";

    switch (command.toLowerCase()) {
      case String s when s.matches(
          "\\bhelp\\b.*"
      ) -> {
        if (expectedParameters(0, null, command)) {
          helpCommand();
        }
      }

      case String s when s.matches(
          "\\blogout\\b.*"
      ) -> {
        if (expectedParameters(0, null, command)) {
          logoutCommand(serverFacade);
        }
      }

      case String s when s.matches(
          "\\bcreate\\b.*"
      ) -> {
        if (expectedParameters(1, createRegex, command)) {
          createCommand(serverFacade, command);
        }
      }

      case String s when s.matches(
          "\\blist\\b.*"
      ) -> {
        if (expectedParameters(0, null, command)) {
          listCommand(serverFacade);
        }
      }

      case String s when s.matches(
          "\\bjoin\\b.*"
      ) -> {
        if (expectedParameters(2, joinRegex, command)) {
          joinCommand(serverFacade, command);
        }
      }

      case String s when s.matches(
          "\\bobserve\\b.*"
      ) -> {
        if (expectedParameters(1, observeRegex, command)) {
          int gameId = Integer.parseInt(command.split("\\s")[1]);
          observeCommand(gameId);
        }
      }

      case String s when s.matches(
          "\\bclear\\b.*"
      ) -> {
        if (expectedParameters(0, null, command)) {
          clearCommand();
        }
      }

      default -> {
        if (
            !command.matches("\\bquit\\b\\s*\\S+")
                && !command.matches("\\bexit\\b\\s*\\S+")
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

  public boolean processRequest(String userResponse) {

    if (checkForQuit(userResponse)) {
      return true;
    }

    userResponse = userResponse.toLowerCase();

    if (userStatus.equals("LOGGED_OUT")) {
      loggedOutCommands(userResponse);
    } else {
      loggedInCommands(userResponse);
    }

    return false;
  }

}

