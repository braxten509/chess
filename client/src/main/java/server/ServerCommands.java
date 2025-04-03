package server;

import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import model.AuthData;
import model.GameData;
import ui.SpacingType;

/**
 * Class that contains all the commands that can be executed by the user,
 * including some helper methods.
 */
public class ServerCommands {

  public static String userStatus = "LOGGED_OUT";
  private static final Scanner SCANNER = new Scanner(System.in);
  private static String authToken = "";

  private static final HashMap<Integer, Integer> LISTED_GAMES = new HashMap<>();

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
   * Command used to clear the client's terminal.
   */
  public static void clearCommand() {
    printf("", SpacingType.REGULAR, ERASE_SCREEN);
    System.out.flush();
    printf(
        "♕ Welcome to Chess! Type 'help' to get started ♕",
        SpacingType.UNDER,
        null
    );
  }

  /**
   * Helper method to check for when the user tries to exit a text input area.
   *
   * @param response the response the user gives to the input request
   * @return true or false based on if decides to quit or not (checks for "quit" and "exit")
   */
  public static boolean checkForQuit(String response) {
    return (
        response.equalsIgnoreCase("quit") | response.equalsIgnoreCase("exit")
      );
  }

  /**
   * Lists available commands for the user.
   */
  public static void helpCommand() {
    printf(
        "AVAILABLE COMMANDS",
        SpacingType.SURROUND,
        SET_TEXT_COLOR_YELLOW + SET_TEXT_BOLD
    );
    printf("help", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : lists all available commands", SpacingType.REGULAR, null);
    if (userStatus.equals("LOGGED_OUT")) {
      printf(
          "login <username> <password>",
          SpacingType.NONE,
          SET_TEXT_COLOR_YELLOW
      );
      printf(" : login an existing user", SpacingType.REGULAR, null);
      printf(
          "register <username> <password> <email>",
          SpacingType.NONE,
          SET_TEXT_COLOR_YELLOW
      );
      printf(" : register a new user", SpacingType.REGULAR, null);
    } else {
      printf("logout", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : logout of current session", SpacingType.REGULAR, null);
      printf("create <name>", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : create a new game", SpacingType.REGULAR, null);
      printf("list", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : list games", SpacingType.REGULAR, null);
      printf(
          "join <ID> [WHITE|BLACK]",
          SpacingType.NONE,
          SET_TEXT_COLOR_YELLOW
      );
      printf(" : join an existing game", SpacingType.REGULAR, null);
      printf("observe <ID>", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
      printf(" : watch an ongoing game", SpacingType.REGULAR, null);
    }
    printf("clear", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : clears the current terminal", SpacingType.REGULAR, null);
    printf("quit", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" or ", SpacingType.NONE, null);
    printf("exit", SpacingType.NONE, SET_TEXT_COLOR_YELLOW);
    printf(" : exit the program", SpacingType.UNDER, null);
  }

  /**
   * Command to log in the user.
   *
   * @param serverFacade serverFacade to use
   * @param input user input to use
   */
  public static void loginCommand(ServerFacade serverFacade, String input) {
    String username = input.split("\\s")[1];
    String password = input.split("\\s")[2];

    try {
      AuthData authData = serverFacade.loginUser(username, password);
      authToken = authData.authToken();
      loadValues(serverFacade);

      printf(
          "Success! You have been logged in",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_GREEN
      );
      userStatus = username.toUpperCase();
    } catch (Exception e) {
      printf(
          "Error: Invalid credentials",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_RED
      );
    }
  }

  /**
   * Command to log out the user.
   *
   * @param serverFacade serverFacade to use
   */
  public static void logoutCommand(ServerFacade serverFacade) {
    try {
      serverFacade.logoutUser(authToken);
      authToken = "";
      userStatus = "LOGGED_OUT";
      printf("Logout successful!", SpacingType.SURROUND, SET_TEXT_COLOR_GREEN);
    } catch (Exception e) {
      printf(
          "Error logging out: " + e.getMessage(),
          SpacingType.SURROUND,
          SET_TEXT_COLOR_RED
      );
    }
  }

  /**
   * Command used to register a new user.
   *
   * @param serverFacade serverFacade to use
   * @param input user input to use
   */
  public static void registerCommand(ServerFacade serverFacade, String input) {
    String username = input.split("\\s")[1];
    String password = input.split("\\s")[2];
    String email = input.split("\\s")[3];

    printf("\nPlease confirm Password: ", SpacingType.NONE, SET_TEXT_BOLD);
    String confirmedPassword = SCANNER.next();

    if (!password.equals(confirmedPassword)) {
      printf(
          "Error: Passwords do not match.",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_RED
      );
      return;
    }

    try {
      serverFacade.registerUser(username, password, email);

      AuthData authData = serverFacade.loginUser(username, password);
      authToken = authData.authToken();

      printf(
          "Success! You have been registered and logged in",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_GREEN
      );
      userStatus = username.toUpperCase();
    } catch (Exception e) {
      if (
          e
              .getMessage()
              .equals(
                  "java.lang.RuntimeException: (403) ERROR: {\"message\":\"Error: already taken\"}"
              )
      ) {
        printf(
            "Error: Username already taken",
            SpacingType.REGULAR,
            SET_TEXT_COLOR_RED
        );
      }
    }
  }

  /**
   * Command used to create a new game.
   *
   * @param serverFacade serverFacade to use
   * @param input user input to use
   */
  public static void createCommand(ServerFacade serverFacade, String input) {
    String gameName = input.split("\\s+")[1];
    try {
      serverFacade.createGame(authToken, gameName);
      loadValues(serverFacade);
      printf(
          "Success creating game with name '" + gameName + "'!",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_GREEN
      );
    } catch (Exception e) {
      printf(
          "Error creating game: " + e.getMessage(),
          SpacingType.SURROUND,
          SET_TEXT_COLOR_RED
      );
    }
  }

  /**
   * Help command that displays the screen the player sees once in a game.
   *
   * @param playerColor color of the player that joined
   */
  private static void inGame(String playerColor) {
    String[][] chessPieceGrid = getChessPieceGrid(playerColor);

    int squareColor = (playerColor.equals("WHITE")) ? 1 : 0;
    for (int indexX = 8; indexX > 0; indexX--) {
      for (int indexY = 0; indexY < 8; indexY++) {
        int currentColor = squareColor % 2;
        String currentPiece = chessPieceGrid[indexX - 1][indexY];
        if (currentColor == 1) {
          if (!currentPiece.equalsIgnoreCase(" ")) {
            printf("", SpacingType.NONE, SET_BG_COLOR_DARK_GREY);

            printf(currentPiece, SpacingType.NONE, SET_BG_COLOR_DARK_GREY);

            printf("", SpacingType.NONE, SET_BG_COLOR_DARK_GREY);
          } else {
            printf(" ", SpacingType.NONE, SET_BG_COLOR_DARK_GREY);

            printf(currentPiece, SpacingType.NONE, SET_BG_COLOR_DARK_GREY);

            printf(" ", SpacingType.NONE, SET_BG_COLOR_DARK_GREY);
          }
        } else {
          if (!currentPiece.equalsIgnoreCase(" ")) {
            printf("", SpacingType.NONE, SET_BG_COLOR_LIGHT_GREY);

            printf(currentPiece, SpacingType.NONE, SET_BG_COLOR_LIGHT_GREY);

            printf("", SpacingType.NONE, SET_BG_COLOR_LIGHT_GREY);
          } else {
            printf(" ", SpacingType.NONE, SET_BG_COLOR_LIGHT_GREY);

            printf(currentPiece, SpacingType.NONE, SET_BG_COLOR_LIGHT_GREY);

            printf(" ", SpacingType.NONE, SET_BG_COLOR_LIGHT_GREY);
          }
        }
        squareColor += 1;
      }
      squareColor += 1;
      printf("", SpacingType.REGULAR, null);
    }

    printf("", SpacingType.REGULAR, null);

    // game loop
    while (true) {
      System.out.print("[" + userStatus + " - " + playerColor + "] >>> ");
      String response = SCANNER.next();

      if (checkForQuit(response)) {
        break;
      }
    }

    printf("", SpacingType.REGULAR, null);
  }

  private static String[][] getChessPieceGrid(String playerColor) {
    String[][] chessPieceGrid = {{}};
    if (playerColor.equalsIgnoreCase("white")) {
      // starts at bottom 0,0
      chessPieceGrid = new String[][]{
          {
              WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
              WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK,
          },
          {
              WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN,
              WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN,
          },
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {
              BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN,
              BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN,
          },
          {
              BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
              BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK,
          },
      };
    } else if (playerColor.equalsIgnoreCase("black")) {
      // starts at bottom 0,0
      chessPieceGrid = new String[][]{
          {
              BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
              BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK,
          },
          {
              BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN,
              BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN,
          },
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {" ", " ", " ", " ", " ", " ", " ", " "},
          {
              WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN,
              WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN,
          },
          {
              WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
              WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK,
          },
      };
    }
    return chessPieceGrid;
  }

  /**
   * Command that connects the user to an active game.
   *
   * @param serverFacade serverFacade to use
   * @param input user input to use
   */
  public static void joinCommand(ServerFacade serverFacade, String input) {
    int listedGameId = Integer.parseInt(input.split("\\s+")[1]);
    String playerColor = (input.split("\\s+")[2]).toUpperCase();

    if (listedGameId > LISTED_GAMES.size()) {
      printf(
          "Error: Game does not exist",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_RED
      );
      return;
    }

    int trueGameId = LISTED_GAMES.get(listedGameId);

    try {
      serverFacade.joinGame(authToken, playerColor, trueGameId);
      printf(
          "Success joining game!",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_GREEN
      );
      inGame(playerColor);
    } catch (Exception e) {
      printf(
          "Error joining game: " + e.getMessage(),
          SpacingType.SURROUND,
          SET_TEXT_COLOR_RED
      );
    }
  }

  /**
   * Command to observe a game.
   *
   * @param id of the game
   */
  public static void observeCommand(int id) {
    if (LISTED_GAMES.containsKey(id)) {
      printf("Now observing game" + id, SpacingType.SURROUND, SET_TEXT_BOLD);
    } else {
      printf("Error: game does not exist", SpacingType.SURROUND, SET_TEXT_COLOR_RED);
    }
  }

  /**
   * Command to list all active games.
   *
   * @param serverFacade serverFacade to use
   */
  public static void listCommand(ServerFacade serverFacade) {
    printf(
        "CURRENT GAMES",
        SpacingType.ABOVE,
        SET_TEXT_COLOR_YELLOW + SET_TEXT_BOLD
    );
    ArrayList<GameData> games = serverFacade.listGames(authToken).games();

    LISTED_GAMES.clear();
    int gameNumber = 1;
    for (GameData game : games) {
      String playerWhite = "EMPTY";
      String playerBlack = "EMPTY";

      int playerCount = 0;
      if (game.whiteUsername() != null) {
        playerWhite = game.whiteUsername().toUpperCase();
        playerCount += 1;
      }
      if (game.blackUsername() != null) {
        playerBlack = game.blackUsername().toUpperCase();
        playerCount += 1;
      }
      printf(
          gameNumber + ". " + game.gameName() + " ",
          SpacingType.NONE,
          SET_TEXT_COLOR_BLUE
      );
      if (playerCount < 2) {
        printf(playerCount + "/2", SpacingType.REGULAR, SET_TEXT_COLOR_GREEN);
      } else {
        printf(playerCount + "/2", SpacingType.REGULAR, SET_TEXT_COLOR_YELLOW);
      }
      printf("   " + playerWhite, SpacingType.REGULAR, null);
      printf(
          "   " + playerBlack,
          SpacingType.REGULAR,
          SET_TEXT_COLOR_LIGHT_GREY
      );

      LISTED_GAMES.put(gameNumber, game.gameID());
      gameNumber += 1;
    }
    printf("", SpacingType.REGULAR, null);
  }

  private static void loadValues(ServerFacade serverFacade) {
    ArrayList<GameData> games = serverFacade.listGames(authToken).games();
    LISTED_GAMES.clear();
    int gameNumber = 1;

    for (GameData game : games) {
      LISTED_GAMES.put(gameNumber, game.gameID());
      gameNumber += 1;
    }
  }
}
