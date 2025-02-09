package chess;

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
   * This equals piecePossibleMoves minus movesThatPutTheKingInCheck
   * Also piecePossibleMoves minus movesThatDontGetKingOutOfCheckIfInCheck
   * @param startPosition the piece to get valid moves for
   * @return Set of valid moves for requested piece, or null if no piece at
   * startPosition
   */
  public Collection<ChessMove> validMoves(ChessPosition startPosition) {
    final ChessPiece piece = board.getPiece(startPosition);

    if (piece == null) {
      return null;
    }

    final TeamColor teamColor = piece.getTeamColor();
    final HashSet<ChessMove> possibleMoves = new HashSet<>(board.getPiece(startPosition).pieceMoves(board, startPosition));
    HashSet<ChessMove> invalidMoves = new HashSet<>();

    for (ChessMove move : possibleMoves) {
      if (willBeInCheck(teamColor, move)) {
        invalidMoves.add(move);
      }
    }

    possibleMoves.removeAll(invalidMoves);

    return possibleMoves;
  }

  /**
   * Makes a move in a chess game. Takes into account only valid moves
   *
   * @param move chess move to preform
   * @throws InvalidMoveException if move is invalid
   */
  public void makeMove(ChessMove move) throws InvalidMoveException {
    final ChessPosition start = move.getStartPosition();
    final ChessPosition end = move.getEndPosition();

    final ChessPiece movingPiece = board.getPiece(start);
    if (movingPiece == null) {
      throw new InvalidMoveException("Piece doesn't exist");
    }

    if (board.getPiece(start).getTeamColor() != getTeamTurn()) {
      throw new InvalidMoveException("Not this team's turn");
    }

    if (!validMoves(start).contains(move)) {
      throw new InvalidMoveException("Invalid move");
    }


    final TeamColor teamColor = board.getPiece(start).getTeamColor();
    final ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

    if (promotionPiece != null) {
      board.addPiece(end, new ChessPiece(teamColor, promotionPiece));
    } else {
      board.addPiece(end, movingPiece);
    }

    board.removePiece(start);

    final TeamColor enemyColor = (teamColor == TeamColor.WHITE)
            ? TeamColor.BLACK
            : TeamColor.WHITE;

    setTeamTurn(enemyColor);
  }

  public void makeUnrestrictedMove(ChessMove move, ChessBoard board) {
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
  private ArrayList<ChessMove> getPossibleMoves(
    TeamColor teamColor,
    ChessBoard board
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
   * @param teamColor of the team
   * @return collection of enemy moves for every piece
   */
  private HashSet<ChessMove> getTeamsValidMoves(
          TeamColor teamColor,
          ChessBoard board
  ) {
    var teamsValidMoves = new HashSet<ChessMove>();

    for (int x = 1; x <= 8; x++) {
      for (int y = 1; y <= 8; y++) {
        var position = new ChessPosition(x, y);
        var piece = board.getPiece(position);

        if (piece == null) continue;

        var pieceColor = piece.getTeamColor();

        if (pieceColor != teamColor) continue;

        teamsValidMoves.addAll(validMoves(position));
      }
    }

    return teamsValidMoves;
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
    var enemyPossibleMoves = getPossibleMoves(enemyColor, board);

    for (ChessMove move : enemyPossibleMoves) {
      if (move.getEndPosition().equals(thisKingsPosition)) {
        return true;
      }
    }

    return false;
  }

  public boolean willBeInCheck(TeamColor teamColor, ChessMove move) {
    final ChessBoard copyBoard = board.copyBoard();

    makeUnrestrictedMove(move, copyBoard);

    final TeamColor enemyColor = (teamColor == TeamColor.WHITE)
      ? TeamColor.BLACK
      : TeamColor.WHITE;

    var enemyPossibleMoves = getPossibleMoves(enemyColor, copyBoard);
    final ChessPosition thisKingsPosition = findKing(teamColor, copyBoard);

    return enemyPossibleMoves
            .stream()
            .anyMatch(enemyMove -> enemyMove.getEndPosition().equals(thisKingsPosition));
  }

  /**
   * This includes checking to make sure he does
   * not move into check
   * @param teamColor team color
   * @param board current or theoretical board
   * @return if he can move or not boolean
   */
  private boolean canKingMove(TeamColor teamColor, ChessBoard board) {
    final boolean CANNOT_MOVE = false;
    final boolean CAN_MOVE = true;

    final ChessPosition kingPosition = findKing(teamColor, board);
    final Collection<ChessMove> kingMoves = board
      .getPiece(kingPosition)
      .pieceMoves(board, kingPosition);

    final TeamColor enemyColor = (teamColor == TeamColor.WHITE)
      ? TeamColor.BLACK
      : TeamColor.WHITE;
    final Collection<ChessMove> enemyPossibleMoves = getPossibleMoves(
      enemyColor,
      board
    );

    Set<ChessPosition> kingEndPositions = new HashSet<>();
    for (ChessMove move : kingMoves) {
      kingEndPositions.add(move.getEndPosition());
    }

    Set<ChessPosition> enemyEndPositions = new HashSet<>();
    for (ChessMove move : enemyPossibleMoves) {
      enemyEndPositions.add(move.getEndPosition());
    }

    if (enemyEndPositions.containsAll(kingEndPositions)) {
      return CANNOT_MOVE;
    }

    for (ChessMove move : kingMoves) {
      if (!willBeInCheck(teamColor, move)) {
        return CAN_MOVE;
      }
    }

    return CANNOT_MOVE;
  }

  /**
   * Determines if the given team is in checkmate
   *
   * @param teamColor which team to check for checkmate
   * @return True if the specified team is in checkmate
   */
  public boolean isInCheckmate(TeamColor teamColor) {
    final boolean IN_CHECKMATE = true;
    final boolean NOT_IN_CHECKMATE = false;

    if (canKingMove(teamColor, board)) {
      return NOT_IN_CHECKMATE;
    }

    if (getTeamsValidMoves(teamColor, board).isEmpty() && isInCheck(teamColor)) {
      return IN_CHECKMATE;
    }

    return NOT_IN_CHECKMATE;
  }

  /**
   * Determines if the given team is in stalemate, which here is defined as having
   * no valid moves
   *
   * @param teamColor which team to check for stalemate
   * @return True if the specified team is in stalemate, otherwise false
   */
  public boolean isInStalemate(TeamColor teamColor) {
    final boolean IN_STALEMATE = true;
    final boolean NOT_IN_STALEMATE = false;

    if (getTeamsValidMoves(teamColor, board).isEmpty() && !isInCheck(teamColor)) {
      return IN_STALEMATE;
    }

    return NOT_IN_STALEMATE;
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
