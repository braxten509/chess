package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor turn = TeamColor.WHITE;
    ChessBoard board = new ChessBoard();

    public ChessGame() {

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
        BLACK
    }

    /**
     * TODO: THIS METHOD MUST ACCOUNT FOR CHECK!
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
     * TODO: TAKE INTO ACCOUNT ONLY VALID MOVES
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
            return;
            //throw new InvalidMoveException("Invalid move");
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
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var enemyPossibleMoves = new ArrayList<ChessMove>();
        ChessPosition thisKingsPosition = null;

        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                var checkingPosition = new ChessPosition(x, y);
                var checkingPiece = board.getPiece(checkingPosition);

                if (checkingPiece == null) continue;

                var checkingPieceColor = checkingPiece.getTeamColor();
                var checkingPieceType = checkingPiece.getPieceType();

                if (checkingPieceColor == teamColor && checkingPieceType == ChessPiece.PieceType.KING) {
                   thisKingsPosition = checkingPosition;
                }

                if (checkingPieceColor == teamColor) continue;

                enemyPossibleMoves.addAll(checkingPiece.pieceMoves(board, checkingPosition));
            }
        }

        final ChessPosition finalThisKingsPosition = thisKingsPosition;
        return enemyPossibleMoves.stream().anyMatch(move -> move.getEndPosition().equals(finalThisKingsPosition));
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
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
