package client;

import client.formatting.EscapeSequences;
import client.formatting.SpacingType;

import static client.ChessClient.expectedParameters;
import static client.ServerCommands.*;
import static client.ServerCommands.printf;

public class RequestProcessor {

  private final ServerFacade serverFacade;

  public RequestProcessor(ServerFacade serverFacade) {
    this.serverFacade = serverFacade;
  }

  public boolean processRequest(String userResponse) {


    if (checkForQuit(userResponse)) {
      return true;
    }

    var loginRegex = "login\\s+[a-zA-Z0-9]+\\s+[^);]+";
    var registerRegex = "register\\s+[a-zA-Z0-9]+\\s+[^);]+\\s+[a-zA-Z0-9@.&]+";
    var createRegex = "create\\s+[a-zA-Z0-9]{1,20}";
    var joinRegex = "join\\s+[0-9]+\\s+(white|black)";
    var observeRegex = "observe\\s+[0-9]+";

    userResponse = userResponse.toLowerCase();

    if (userStatus.equals("LOGGED_OUT")) {
      switch (userResponse) {
        case String s when s.matches(
            "\\bhelp\\b.*"
        ) -> {
          if (expectedParameters(0, null, userResponse)) {
            helpCommand();
          }
        }

        case String s when s.matches(
            "\\blogin\\b.*"
        ) -> {
          if (expectedParameters(2, loginRegex, userResponse)) {
            loginCommand(serverFacade, userResponse);
          }
        }

        case String s when s.matches(
            "\\bregister\\b.*"
        ) -> {
          if (expectedParameters(3, registerRegex, userResponse)) {
            registerCommand(serverFacade, userResponse);
          }
        }

        case String s when s.matches(
            "\\bclear\\b.*"
        ) -> {
          if (expectedParameters(0, null, userResponse)) {
            clearCommand();
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
            helpCommand();
          }
        }

        case String s when s.matches(
            "\\blogout\\b.*"
        ) -> {
          if (expectedParameters(0, null, userResponse)) {
            logoutCommand(serverFacade);
          }
        }

        case String s when s.matches(
            "\\bcreate\\b.*"
        ) -> {
          if (expectedParameters(1, createRegex, userResponse)) {
            createCommand(serverFacade, userResponse);
          }
        }

        case String s when s.matches(
            "\\blist\\b.*"
        ) -> {
          if (expectedParameters(0, null, userResponse)) {
            listCommand(serverFacade);
          }
        }

        case String s when s.matches(
            "\\bjoin\\b.*"
        ) -> {
          if (expectedParameters(2, joinRegex, userResponse)) {
            joinCommand(serverFacade, userResponse);
          }
        }

        case String s when s.matches(
            "\\bobserve\\b.*"
        ) -> {
          if (expectedParameters(1, observeRegex, userResponse)) {
            int gameId = Integer.parseInt(userResponse.split("\\s")[1]);
            observeCommand(gameId);
          }
        }

        case String s when s.matches(
            "\\bclear\\b.*"
        ) -> {
          if (expectedParameters(0, null, userResponse)) {
            clearCommand();
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

    return false;
  }
}
