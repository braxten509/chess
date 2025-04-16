package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.formatting.SpacingType;
import client.facade.WebSocketFacade;
import model.GameData;
import org.apache.commons.lang.ArrayUtils;

import java.util.Scanner;

import static client.ChessClient.*;
import static client.ChessClient.printf;
import static client.handler.CommandHandler.checkForQuit;
import static client.formatting.EscapeSequences.*;
import static client.formatting.EscapeSequences.BLACK_KING;

public class GameClient {

  private final String username;
  private final String playerColor;
  private final WebSocketFacade webSocketFacade;
  private GameData initialGameData;

  public GameClient(String username, String playerColor, GameData initialGameData, WebSocketFacade webSocketFacade) {
    this.username = username;
    this.playerColor = playerColor;
    this.webSocketFacade = webSocketFacade;
    this.initialGameData = initialGameData;
  }

  public void run() {
    Scanner scanner = new Scanner(System.in);

    drawChessboard(playerColor, initialGameData);

    printf(
        "Success joining game as " + playerColor.toUpperCase() + "!",
        SpacingType.UNDER,
        SET_TEXT_COLOR_GREEN
    );

    while (true) {
      printf("[(" + playerColor.toUpperCase() + ") " + userStatus + "] >>> ", SpacingType.NONE, null);
      String response = scanner.nextLine();

      if (checkForQuit(response)) {
        printf("", SpacingType.REGULAR, null);
        break;
      }
    }
  }

  /**
   * Draws the chessboard for the client
   *
   * @param playerColor playerColor
   * @param gameData gameData
   */
  public static void drawChessboard(String playerColor, GameData gameData) {
    String[][] chessPieceGrid = getChessPieceGrid(playerColor, gameData);

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
  }

  private static String[][] getChessPieceGrid(String playerColor, GameData gameData) {
    String[][] chessPieceGrid = new String[8][8];

    ChessBoard chessBoard = gameData.game().getBoard();

    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        ChessPiece currentPiece = chessBoard.getPiece(new ChessPosition(row + 1, col + 1));

        if (currentPiece == null) {
          chessPieceGrid[row][col] = " ";
          continue;
        }

        if (currentPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
          switch (currentPiece.getPieceType()) {
            case PAWN -> chessPieceGrid[row][col] = WHITE_PAWN;
            case ROOK -> chessPieceGrid[row][col] = WHITE_ROOK;
            case KNIGHT -> chessPieceGrid[row][col] = WHITE_KNIGHT;
            case BISHOP -> chessPieceGrid[row][col] = WHITE_BISHOP;
            case QUEEN -> chessPieceGrid[row][col] = WHITE_QUEEN;
            case KING -> chessPieceGrid[row][col] = WHITE_KING;
            case null, default -> chessPieceGrid[row][col] = " ";
          }
        } else {
          switch (currentPiece.getPieceType()) {
            case PAWN -> chessPieceGrid[row][col] = BLACK_PAWN;
            case ROOK -> chessPieceGrid[row][col] = BLACK_ROOK;
            case KNIGHT -> chessPieceGrid[row][col] = BLACK_KNIGHT;
            case BISHOP -> chessPieceGrid[row][col] = BLACK_BISHOP;
            case QUEEN -> chessPieceGrid[row][col] = BLACK_QUEEN;
            case KING -> chessPieceGrid[row][col] = BLACK_KING;
            case null, default -> chessPieceGrid[row][col] = " ";
          }
        }
      }
    }

    if (playerColor.equals("BLACK")) {
      ArrayUtils.reverse(chessPieceGrid);
    }

    /*if (playerColor.equalsIgnoreCase("white") || playerColor.equalsIgnoreCase("observer")) {
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
    } */
    return chessPieceGrid;
  }
}
