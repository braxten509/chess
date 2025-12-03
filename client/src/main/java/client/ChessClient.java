package client;

import static ui.EscapeSequences.RESET_ALL;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

import client.facade.ServerFacade;
import client.facade.WebSocketFacade;
import client.handler.ChessMessageHandler;
import client.handler.UserInputHandler;
import java.util.Scanner;
import ui.SpacingType;

public class ChessClient {

  public static String userStatus = "LOGGED_OUT";
  public static boolean inGame = false;
  private final int port;

  public ChessClient(int port) {
    this.port = port;
  }

  /**
   * Returns the same output as System.out.println()
   *
   * @param text text to print (everything will be formatted as specified)
   */
  public static void printf(String text) {
    System.out.println(text);
  }

  /**
   * Returns the same output as System.out.print() but with formatting
   *
   * @param text text to print (everything will be formatted as specified)
   * @param spacing specifies spacing around text
   * @param formatting formatting. Include multiple with '+'
   */
  public static void printf(String text, SpacingType spacing, String formatting) {
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
      case null, default -> {}
    }

    if (formatting != null) {
      System.out.print(formatting);
    }

    System.out.print(spacingBeginning + text + spacingEnding);

    System.out.print(RESET_ALL);
  }

  /** Runs the entire client program. */
  public void run() {
    String url = "http://localhost:" + port;
    WebSocketFacade webSocketFacade;

    try {
      webSocketFacade = new WebSocketFacade(url, new ChessMessageHandler());
      ServerFacade serverFacade = new ServerFacade(port);
      UserInputHandler requestProcessor = new UserInputHandler(serverFacade, webSocketFacade);

      Scanner scanner = new Scanner(System.in);

      printf("♕ Welcome to Chess! Type 'help' to get started ♕", SpacingType.UNDER, null);

      while (true) {
        if (!inGame) {
          System.out.print("[" + userStatus + "] >>> ");
          String userResponse = scanner.nextLine();

          /* Executes command user inputs. Also checks to see if a request to quit is given */
          if (requestProcessor.processRequest(userResponse)) {
            break;
          }
        }
      }
    } catch (Exception exception) {
      printf("ERROR: " + exception, SpacingType.ABOVE, SET_TEXT_COLOR_RED);
      printf(exception.getMessage(), SpacingType.UNDER, SET_TEXT_COLOR_RED);
    }
  }
}
