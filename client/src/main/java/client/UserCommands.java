package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import client.websocket.WebSocketFacade;
import model.AuthData;
import model.GameData;
import client.formatting.SpacingType;
import websocket.commands.UserGameCommand;

import static client.ChessClient.printf;
import static client.ChessClient.userStatus;
import static client.formatting.EscapeSequences.*;

/**
 * Class that contains all the commands that can be executed by the user,
 * including some helper methods.
 */
public class UserCommands {

  private static final Scanner SCANNER = new Scanner(System.in);
  private static String authToken = "";

  private static final HashMap<Integer, Integer> LISTED_GAMES = new HashMap<>();

  private static void inGame(String playerColor) {
    String[][] chessPieceGrid = getChessPieceGrid(playerColor);

    String[] whiteSpaceNumbers = "1 2 3 4 5 6 7 8".split("\\s");
    String[] whiteSpaceLetters = "a b c d e f g h".split("\\s");

    String[] blackSpaceNumbers = "8 7 6 5 4 3 2 1".split("\\s");
    String[] blackSpaceLetters = "h g f e d c b a".split("\\s");

    for (int index = -2; index < 8; index++) {
      if (index >= 0 && (playerColor.equals("WHITE") || playerColor.equals("OBSERVER"))) {
        printf(" " + whiteSpaceLetters[index] + " ", SpacingType.NONE, null);
      } else if (index >= 0 && playerColor.equals("BLACK")) {
        printf(" " + blackSpaceLetters[index] + " ", SpacingType.NONE, null);
      } else {
        printf(" ", SpacingType.NONE, null);
      }
    }

    printf("", SpacingType.REGULAR, null);

    /* BlackSquare = 1, WhiteSquare = 0 */
    int squareColor = (playerColor.equals("WHITE") || playerColor.equals("OBSERVER")) ? 1 : 0;
    for (int indexX = 8; indexX > 0; indexX--) {

      if (playerColor.equals("WHITE") || playerColor.equals("OBSERVER")) {
        printf(whiteSpaceNumbers[indexX - 1] + " ", SpacingType.NONE, null);
      } else {
        printf(blackSpaceNumbers[indexX - 1] + " ", SpacingType.NONE, null);
      }

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

      if (playerColor.equals("WHITE") || playerColor.equals("OBSERVER")) {
        printf(" " + whiteSpaceNumbers[indexX - 1] + " ", SpacingType.NONE, null);
      } else {
        printf(" " + blackSpaceNumbers[indexX - 1] + " ", SpacingType.NONE, null);
      }

      squareColor += 1;
      printf("", SpacingType.REGULAR, null);
    }

    for (int index = -2; index < 8; index++) {
      if (index >= 0 && (playerColor.equals("WHITE") || playerColor.equals("OBSERVER"))) {
        printf(" " + whiteSpaceLetters[index] + " ", SpacingType.NONE, null);
      } else if (index >= 0 && playerColor.equals("BLACK")) {
        printf(" " + blackSpaceLetters[index] + " ", SpacingType.NONE, null);
      } else {
        printf(" ", SpacingType.NONE, null);
      }
    }

    printf("", SpacingType.ABOVE, null);

    // game loop
    while (true) {
      System.out.print("[" + userStatus + " - " + playerColor.toUpperCase() + "] >>> ");
      String response = SCANNER.nextLine();

      if (checkForQuit(response)) {
        break;
      }
    }

    printf("", SpacingType.REGULAR, null);
  }

  private static String[][] getChessPieceGrid(String playerColor) {
    String[][] chessPieceGrid = {{}};
    if (playerColor.equalsIgnoreCase("white") || playerColor.equalsIgnoreCase("observer")) {
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

  private static void loadValues(ServerFacade serverFacade) {
    ArrayList<GameData> games = serverFacade.listGames(authToken).games();
    LISTED_GAMES.clear();
    int gameNumber = 1;

    for (GameData game : games) {
      LISTED_GAMES.put(gameNumber, game.gameID());
      gameNumber += 1;
    }
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
   * Helper method to check for when the user tries to exit a text userInput area.
   *
   * @param userInput the response the user gives to the userInput request
   * @return true or false based on if decides to quit or not (checks for "quit" and "exit")
   */
  public static boolean checkForQuit(String userInput) {
    if (userInput.matches("\\bquit\\b\\s*\\S+") || userInput.matches("\\bexit\\b\\s*\\S+")) {
      printf("Improper number of parameters. Expected 0. Use: <quit/exit>",
          SpacingType.SURROUND, SET_TEXT_COLOR_YELLOW);
      return false;
    } else {
      return userInput.matches("\\bquit\\b") || userInput.matches("\\bexit\\b");
    }
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
   * @param userInput user userInput to use
   */
  public static void loginCommand(ServerFacade serverFacade, String userInput) {
    
    String username = userInput.split("\\s")[1];
    String password = userInput.split("\\s")[2];

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
   * @param userInput user userInput to use
   */
  public static void registerCommand(ServerFacade serverFacade, String userInput) {
    String username = userInput.split("\\s")[1];
    String password = userInput.split("\\s")[2];
    String email = userInput.split("\\s")[3];

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
   * @param userInput user userInput to use
   */
  public static void createCommand(ServerFacade serverFacade, String userInput) {
    String gameName = userInput.split("\\s+")[1];
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
   * Command that connects the user to an active game.
   *
   * @param serverFacade serverFacade to use
   * @param userInput user userInput to use
   */
  public static void joinCommand(ServerFacade serverFacade, WebSocketFacade webSocketFacade, String userInput) {
    int userGivenGameID = Integer.parseInt(userInput.split("\\s+")[1]);
    String playerColor = (userInput.split("\\s+")[2]).toUpperCase();

    if (userGivenGameID > LISTED_GAMES.size()) {
      printf(
          "Error: Game does not exist",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_RED
      );
      return;
    }

    int trueGameId = LISTED_GAMES.get(userGivenGameID);

    try {
      serverFacade.joinGame(authToken, playerColor, trueGameId);
      printf(
          "Success joining game!",
          SpacingType.SURROUND,
          SET_TEXT_COLOR_GREEN
      );

      UserGameCommand joinCommand =
          new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, trueGameId);

      webSocketFacade.sendCommand(joinCommand);

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
      inGame("OBSERVER");
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

}
