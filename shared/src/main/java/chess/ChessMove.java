package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public String toString() {

        char startColumnLetter = ' ';
        char endColumnLetter = ' ';

        switch(startPosition.getColumn()) {
            case 1 -> startColumnLetter = 'a';
            case 2 -> startColumnLetter = 'b';
            case 3 -> startColumnLetter = 'c';
            case 4 -> startColumnLetter = 'd';
            case 5 -> startColumnLetter = 'e';
            case 6 -> startColumnLetter = 'f';
            case 7 -> startColumnLetter = 'g';
            case 8 -> startColumnLetter = 'h';
        }

        switch(endPosition.getColumn()) {
            case 1 -> endColumnLetter = 'a';
            case 2 -> endColumnLetter = 'b';
            case 3 -> endColumnLetter = 'c';
            case 4 -> endColumnLetter = 'd';
            case 5 -> endColumnLetter = 'e';
            case 6 -> endColumnLetter = 'f';
            case 7 -> endColumnLetter = 'g';
            case 8 -> endColumnLetter = 'h';
        }

        return "startPosition=" + startColumnLetter + startPosition.getRow() +
                ", endPosition=" + endColumnLetter + endPosition.getRow() +
                ", promotionPiece=" + promotionPiece + '\n';
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
