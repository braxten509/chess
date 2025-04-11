package client;

import client.formatting.SpacingType;

import java.util.Arrays;
import java.util.Scanner;

import static client.ServerCommands.printf;
import static client.ServerCommands.userStatus;
import static client.formatting.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class ChessClient {
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

  public static boolean expectedParameters(
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

  public static void run(int serverPort) {

    ServerFacade serverFacade = new ServerFacade(serverPort);
    RequestProcessor requestProcessor = new RequestProcessor(serverFacade);
    Scanner scanner = new Scanner(System.in);

    printf(
        "♕ Welcome to Chess! Type 'help' to get started ♕",
        SpacingType.UNDER,
        null
    );

    while (true) {
      System.out.print("[" + userStatus + "] >>> ");
      String userResponse = scanner.nextLine();
      /* checks to see if a request to quit is given */
      if (requestProcessor.processRequest(userResponse)) {
        break;
      }
    }
  }
}
