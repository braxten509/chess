package client;

import client.formatting.SpacingType;
import client.websocket.WebSocketFacade;

import java.net.URI;
import java.util.Scanner;

import static client.formatting.EscapeSequences.RESET_ALL;
import static client.formatting.EscapeSequences.SET_TEXT_COLOR_RED;

public class ChessClient {

  private final int port;
  private WebSocketFacade webSocketFacade;

  public static String userStatus = "LOGGED_OUT";

  public ChessClient(int port) {
    this.port = port;
  }

  /**
   * Returns the same output as System.out.print() but with formatting
   *
   * @param text       text to print (everything will be formatted as specified)
   * @param formatting formatting. Include multiple with '+'
   * @param spacing    specifies spacing around text
   */
  public static void printf(
      String text,
      SpacingType spacing,
      String formatting
  ) {
    String spacingBeginning = "";
    String spacingEnding = "\n";

    switch (spacing) {
      case NONE -> spacingEnding = "";
      case ABOVE -> spacingBeginning += "\n";
      case UNDER -> spacingEnding += "\n";
      case SURROUND -> {
        spacingBeginning += "\n";
        spacingEnding += "\n";
      }
      case DOUBLE_SURROUND -> {
        spacingBeginning += "\n\n";
        spacingEnding += "\n\n";
      }
      case null, default -> {
      }
    }

    if (formatting != null) {
      System.out.print(formatting);
    }

    System.out.print(spacingBeginning + text + spacingEnding);

    System.out.print(RESET_ALL);
  }

  /**
   * Runs the entire client program.
   */
  public void run() {
    String url = "http://localhost:" + port;

    try {
      webSocketFacade = new WebSocketFacade(url, new ChessNotificationHandler());
    } catch (Exception exception) {
      printf("ERROR: WebSocket not initiated", SpacingType.ABOVE, SET_TEXT_COLOR_RED);
      printf(exception.getMessage(), SpacingType.UNDER, SET_TEXT_COLOR_RED);
    }

    ServerFacade serverFacade = new ServerFacade(port);
    RequestProcessor requestProcessor = new RequestProcessor(serverFacade, webSocketFacade);
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
