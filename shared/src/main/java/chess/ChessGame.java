package chess;

import com.sun.nio.sctp.SctpSocketOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

  final int UP = 1;
  final int DOWN = -1;
  final int RIGHT = 1;
  final int LEFT = -1;
  final int NOCHANGE = 0;

  TeamColor turn = TeamColor.WHITE;
  ChessBoard board = new ChessBoard();

  // default constructor is used when a new object is created
  public ChessGame() {
    board.resetBoard();
  }

  public ChessGame(TeamColor turn, ChessBoard board) {
    this.turn = turn;
    this.board = board;
  }

  /**
   * @return Which team's turn it is
   */
  public TeamColor getTeamTurn() {
    return turn;
  }

  /**
   * Set's which teams turn it is
   *
   * @param team the team whose turn it is
   */
  public void setTeamTurn(TeamColor team) {
    turn = team;
  }

  /**
   * Enum identifying the 2 possible teams in a chess game
   */
  public enum TeamColor {
    WHITE,
    BLACK,
  }

  /**
   * Gets a valid moves for a piece at the given location
   *
   * @param startPosition the piece to get valid moves for
   * @return Set of valid moves for requested piece, or null if no piece at
   * startPosition
   */
  public Collection<ChessMove> validMoves(ChessPosition startPosition) {
    if (board.getPiece(startPosition) == null) {
      return null;
    }

    return board.getPiece(startPosition).pieceMoves(board, startPosition);
  }

  /**
   * Makes a move in a chess game
   *
   * @param move chess move to preform
   * @throws InvalidMoveException if move is invalid
   */
  public void makeMove(ChessMove move) throws InvalidMoveException {
    final ChessPosition start = move.getStartPosition();
    final ChessPosition end = move.getEndPosition();

    final ChessPiece movingPiece = board.getPiece(start);
    if (movingPiece == null) {
      throw new InvalidMoveException("Invalid move");
    }

    final TeamColor color = board.getPiece(start).getTeamColor();
    final ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

    if (promotionPiece != null) {
      board.addPiece(end, new ChessPiece(color, promotionPiece));
    } else {
      board.addPiece(end, movingPiece);
    }

    board.removePiece(start);
  }

  public void makeAnyMove(ChessMove move, ChessBoard board) {
    final ChessPosition start = move.getStartPosition();
    final ChessPosition end = move.getEndPosition();

    final ChessPiece movingPiece = board.getPiece(start);
    if (movingPiece == null) {
      return;
    }

    final TeamColor color = board.getPiece(start).getTeamColor();
    final ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

    if (promotionPiece != null) {
      board.addPiece(end, new ChessPiece(color, promotionPiece));
    } else {
      board.addPiece(end, movingPiece);
    }

    board.removePiece(start);
  }

  /**
   * @param teamColor of the team
   * @return collection of enemy moves for every piece
   */
  private ArrayList<ChessMove> teamsPossibleMoves(TeamColor teamColor, ChessBoard board
  ) {
    var possibleMoves = new ArrayList<ChessMove>();

    for (int x = 1; x <= 8; x++) {
      for (int y = 1; y <= 8; y++) {
        var checkingPosition = new ChessPosition(x, y);
        var checkingPiece = board.getPiece(checkingPosition);

        if (checkingPiece == null) continue;

        var checkingPieceColor = checkingPiece.getTeamColor();

        if (checkingPieceColor != teamColor) continue;

        possibleMoves.addAll(checkingPiece.pieceMoves(board, checkingPosition));
      }
    }

    return possibleMoves;
  }

  /**
   * @param teamColor of the king in question
   * @return location of requested team's king position
   */
  private ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
    ChessPosition thisKingsPosition = null;

    for (int x = 1; x <= 8; x++) {
      for (int y = 1; y <= 8; y++) {
        var checkingPosition = new ChessPosition(x, y);
        var checkingPiece = board.getPiece(checkingPosition);

        if (checkingPiece == null) continue;

        var checkingPieceColor = checkingPiece.getTeamColor();
        var checkingPieceType = checkingPiece.getPieceType();

        if (
          checkingPieceColor == teamColor &&
          checkingPieceType == ChessPiece.PieceType.KING
        ) {
          thisKingsPosition = checkingPosition;
          break;
        }
      }
    }

    if (thisKingsPosition == null) {
      throw new RuntimeException("No king is on the board");
    }

    return thisKingsPosition;
  }

  /**
   * Determines if the given team is in check
   *
   * @param teamColor which team to check for check
   * @return True if the specified team is in check
   */
  public boolean isInCheck(TeamColor teamColor) {
    final ChessPosition thisKingsPosition = findKing(teamColor, board);

    final TeamColor enemyColor = (teamColor == TeamColor.WHITE)
      ? TeamColor.BLACK
      : TeamColor.WHITE;
    var enemyPossibleMoves = teamsPossibleMoves(enemyColor, board);

    for (ChessMove move : enemyPossibleMoves) {
      if (move.getEndPosition().equals(thisKingsPosition)) {
        return true;
      }
    }

    return false;
  }

  public boolean isInCheckCopy(TeamColor teamColor, ChessBoard board) {
    final ChessPosition thisKingsPosition = findKing(teamColor, board);

    final TeamColor enemyColor = (teamColor == TeamColor.WHITE)
      ? TeamColor.BLACK
      : TeamColor.WHITE;
    var enemyPossibleMoves = teamsPossibleMoves(enemyColor, board);

    return enemyPossibleMoves
      .stream()
      .anyMatch(move -> move.getEndPosition().equals(thisKingsPosition));
  }

  // this also means can he move and not be in check
  private boolean canKingMove(TeamColor teamColor, ChessBoard board) {
    final ChessPosition kingPosition = findKing(teamColor, board);
    final Collection<ChessMove> kingMoves = board
      .getPiece(kingPosition)
      .pieceMoves(board, kingPosition);

    final TeamColor enemyColor = (teamColor == TeamColor.WHITE)
      ? TeamColor.BLACK
      : TeamColor.WHITE;
    final Collection<ChessMove> enemyPossibleMoves = teamsPossibleMoves(
      enemyColor,
      board
    );

    Set<ChessPosition> kingEndPositions = new HashSet<>();
    Set<ChessPosition> enemyEndPositions = new HashSet<>();

    for (ChessMove move : kingMoves) {
      final ChessPosition kingEndPosition = move.getEndPosition();
      kingEndPositions.add(kingEndPosition);
    }

    for (ChessMove move : enemyPossibleMoves) {
      final ChessPosition enemyEndPosition = move.getEndPosition();
      enemyEndPositions.add(enemyEndPosition);
    }

    if (enemyEndPositions.containsAll(kingEndPositions)) {
      return false;
    }

    for (ChessMove move : kingMoves) {
      ChessBoard copyBoard = board.copyBoard();
      makeAnyMove(move, copyBoard);

      if (!isInCheckCopy(teamColor, copyBoard)) {
        return true;
      }
    }

    return false;
  }

  private void print(Object text) {
    System.out.println(text);
  }

  /**
   * Determines if the given team is in checkmate
   *
   * @param teamColor which team to check for checkmate
   * @return True if the specified team is in checkmate
   */
  public boolean isInCheckmate(TeamColor teamColor) {
    print(board);

    if (canKingMove(teamColor, board)) {
      return false;
    }

    Set<ChessMove> thisTeamsMoves = new HashSet<>(
      teamsPossibleMoves(teamColor, board)
    );
    for (ChessMove move : thisTeamsMoves) {
      ChessBoard copyBoard = board.copyBoard();
      makeAnyMove(move, copyBoard);

      if (
        copyBoard.getPiece(move.getEndPosition()) != null &&
        copyBoard.getPiece(move.getEndPosition()).getPieceType() ==
        ChessPiece.PieceType.KING
      ) {
        TeamColor enemyColor = (teamColor == TeamColor.WHITE)
          ? TeamColor.BLACK
          : TeamColor.WHITE;
        var enemyMoves = teamsPossibleMoves(enemyColor, copyBoard);

        for (ChessMove enemyMove : enemyMoves) {
          if (enemyMove.getEndPosition().equals(move.getEndPosition())) {
            return true;
          }
        }
      } else if (canKingMove(teamColor, copyBoard)) {
        print("pass");
        return false;
      }
    }

    return true;
  }

  /**
   * Determines if the given team is in stalemate, which here is defined as having
   * no valid moves
   *
   * @param teamColor which team to check for stalemate
   * @return True if the specified team is in stalemate, otherwise false
   */
  public boolean isInStalemate(TeamColor teamColor) {
    TeamColor enemyColor = (teamColor == TeamColor.WHITE)
      ? TeamColor.BLACK
      : TeamColor.WHITE;
    var thisTeamsMoves = teamsPossibleMoves(teamColor, board);
    var enemyTeamsMoves = teamsPossibleMoves(enemyColor, board);

    return thisTeamsMoves.isEmpty() && enemyTeamsMoves.isEmpty();
  }

  /**
   * Sets this game's chessboard with a given board
   *
   * @param board the new board to use
   */
  public void setBoard(ChessBoard board) {
    this.board.emptyBoard();

    for (int x = 1; x <= 8; x++) {
      for (int y = 1; y <= 8; y++) {
        ChessPosition checkingPosition = new ChessPosition(x, y);
        ChessPiece pieceCopying = board.getPiece(checkingPosition);
        if (pieceCopying == null) {
          continue;
        }
        this.board.addPiece(checkingPosition, board.getPiece(checkingPosition));
      }
    }
  }

  /**
   * Gets the current chessboard
   *
   * @return the chessboard
   */
  public ChessBoard getBoard() {
    return board;
  }
}
