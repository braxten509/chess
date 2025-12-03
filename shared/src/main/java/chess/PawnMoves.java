package chess;

import static chess.ChessPiece.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves {

  /**
   * Calculates the moves a pawn can do excluding en peasant
   *
   * @param board game board
   * @param myPosition initial pawn position
   * @return returns valid moves (either one or two ahead)
   */
  protected static Collection<ChessMove> calculatePawnMoves(
      ChessBoard board, ChessPosition myPosition) {
    var validMoves = new ArrayList<>(calculatePawnMovesWhite(board, myPosition));
    validMoves.addAll(calculatePawnMovesBlack(board, myPosition));

    return validMoves;
  }

  protected static Collection<ChessMove> calculatePawnMovesWhite(
      ChessBoard board, ChessPosition myPosition) {
    var validMoves = new ArrayList<ChessMove>();
    var thisPieceColor = board.getPiece(myPosition).getTeamColor();
    var checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
    if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
      if (isInBounds(myPosition, UP, NO_CHANGE)) {
        checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn());

        if (checkingPosition.getRow() == 8 && board.getPiece(checkingPosition) == null) {
          addMoves(validMoves, myPosition, checkingPosition);
        } else if (board.getPiece(checkingPosition) == null) {
          validMoves.add(new ChessMove(myPosition, checkingPosition, null));
        }
      }

      if (isInBounds(myPosition, UP * 2, NO_CHANGE)) {
        checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn());
        var checkingPositionDoubleMove =
            new ChessPosition(myPosition.getRow() + (UP * 2), myPosition.getColumn());

        if (myPosition.getRow() == 2
            && board.getPiece(checkingPosition) == null
            && board.getPiece(checkingPositionDoubleMove) == null) {
          validMoves.add(new ChessMove(myPosition, checkingPositionDoubleMove, null));
        }
      }

      if (isInBounds(myPosition, UP, RIGHT)) {
        checkingPosition =
            new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn() + RIGHT);

        if (checkingPosition.getRow() == 8
            && board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          addMoves(validMoves, myPosition, checkingPosition);
        } else if (board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          validMoves.add(new ChessMove(myPosition, checkingPosition, null));
        }
      }

      if (isInBounds(myPosition, UP, LEFT)) {
        checkingPosition =
            new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn() + LEFT);

        if (checkingPosition.getRow() == 8
            && board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          addMoves(validMoves, myPosition, checkingPosition);
        } else if (board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          validMoves.add(new ChessMove(myPosition, checkingPosition, null));
        }
      }
    }
    return validMoves;
  }

  public static Collection<ChessMove> calculatePawnMovesBlack(
      ChessBoard board, ChessPosition myPosition) {
    var validMoves = new ArrayList<ChessMove>();
    var thisPieceColor = board.getPiece(myPosition).getTeamColor();
    var checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
    if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
      if (isInBounds(myPosition, DOWN, NO_CHANGE)) {
        checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn());

        if (checkingPosition.getRow() == 1 && board.getPiece(checkingPosition) == null) {
          addMoves(validMoves, myPosition, checkingPosition);
        } else if (board.getPiece(checkingPosition) == null) {
          validMoves.add(new ChessMove(myPosition, checkingPosition, null));
        }
      }

      if (isInBounds(myPosition, DOWN * 2, NO_CHANGE)) {
        checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn());
        var checkingPositionDoubleMove =
            new ChessPosition(myPosition.getRow() + (DOWN * 2), myPosition.getColumn());

        if (myPosition.getRow() == 7
            && board.getPiece(checkingPosition) == null
            && board.getPiece(checkingPositionDoubleMove) == null) {
          validMoves.add(new ChessMove(myPosition, checkingPositionDoubleMove, null));
        }
      }

      if (isInBounds(myPosition, DOWN, RIGHT)) {
        checkingPosition =
            new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn() + RIGHT);

        if (checkingPosition.getRow() == 1
            && board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          addMoves(validMoves, myPosition, checkingPosition);
        } else if (board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          validMoves.add(new ChessMove(myPosition, checkingPosition, null));
        }
      }

      if (isInBounds(myPosition, DOWN, LEFT)) {
        checkingPosition =
            new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn() + LEFT);

        if (checkingPosition.getRow() == 1
            && board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          addMoves(validMoves, myPosition, checkingPosition);
        } else if (board.getPiece(checkingPosition) != null
            && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
          validMoves.add(new ChessMove(myPosition, checkingPosition, null));
        }
      }
    }
    return validMoves;
  }
}
