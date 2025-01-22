package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

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

        int currentCheckingRow = myPosition.getRow();
        int currentCheckingCol = myPosition.getColumn();

        checkingPosition.setRowColumn(currentCheckingRow, currentCheckingCol);

        while (checkingPosition.getRow() + rowChange >= 1 && checkingPosition.getColumn() + colChange >= 1
                && checkingPosition.getRow() + rowChange <= 8 && checkingPosition.getColumn() + colChange <= 8) {

            checkingPosition.setRowColumn(
                    checkingPosition.getRow() + rowChange,
                    checkingPosition.getColumn() + colChange
            );

            if (board.getPiece(checkingPosition) != null) {
                var myPieceColor = board.getPiece(myPosition).pieceColor;
                var otherPieceColor = board.getPiece(checkingPosition).pieceColor;

                if (myPieceColor != otherPieceColor) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, null));
                } else {
                    break;
                }
            }
            validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            currentCheckingRow += rowChange;
            currentCheckingCol += colChange;
        }

        return validMoves;
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
            // diagonal left
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, DOWN, RIGHT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, LEFT));
            validMoves.addAll(checkAndReturnMoves(board, myPosition, UP, RIGHT));
        }

        return validMoves;
    }
}
