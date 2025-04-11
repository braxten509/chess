package client;

import client.formatting.SpacingType;

import java.util.Arrays;
import java.util.Scanner;

import static client.ServerCommands.printf;
import static client.ServerCommands.userStatus;
import static client.formatting.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class ChessClient {

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
      /* Executes command user inputs. Also checks to see if a request to quit is given */
      if (requestProcessor.processRequest(userResponse)) {
        break;
      }
    }
  }
}
