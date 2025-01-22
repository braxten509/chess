package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    private Collection<ChessMove> checkAndReturnMoves(ChessBoard board, ChessPosition myPosition, int rowChange, int colChange) {

        var validMoves = new ArrayList<ChessMove>();
        var checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        while (true) {
            // incrementing happens here
            int newRow = checkingPosition.getRow() + rowChange;
            int newCol = checkingPosition.getColumn() + colChange;

            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                break;
            }

            // if new coordinates are within bounds, apply them
            // replace checking position with a new object so as not to reference the same one in memory
            checkingPosition = new ChessPosition(newRow, newCol);
            ChessPiece otherPiece = board.getPiece(checkingPosition);

            if (otherPiece == null) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
                continue;
            }

            if (board.getPiece(myPosition).getTeamColor() != board.getPiece(checkingPosition).getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
            break;
        }

        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        var selectedPieceType = board.getPiece(myPosition);
        var validMoves = new ArrayList<ChessMove>();

        final int UP = 1;
        final int DOWN = -1;
        final int RIGHT = 1;
        final int LEFT = -1;
        final int NOCHANGE = 0;

        if (selectedPieceType.type == PieceType.BISHOP) {
            // diagonal
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, RIGHT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, RIGHT));
        }

        if (selectedPieceType.type == PieceType.ROOK) {
            // horizontal
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, NOCHANGE));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, NOCHANGE));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, NOCHANGE, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, NOCHANGE, RIGHT));
        }

        if (selectedPieceType.type == PieceType.QUEEN) {
            // diagonal
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, RIGHT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, RIGHT));

            // horizontal
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, NOCHANGE));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, NOCHANGE));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, NOCHANGE, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, NOCHANGE, RIGHT));
        }

        /*
         * ADD PAWN, KNIGHT, & KING (ONLY TESTS LEFT)
         */

        return validMoves;
    }
}
