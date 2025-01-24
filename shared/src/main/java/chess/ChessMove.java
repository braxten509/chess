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

    private char numberToLetter(int number) {
        char returnedLetter = ' ';

        switch(number) {
            case 1 -> returnedLetter = 'a';
            case 2 -> returnedLetter = 'b';
            case 3 -> returnedLetter = 'c';
            case 4 -> returnedLetter = 'd';
            case 5 -> returnedLetter = 'e';
            case 6 -> returnedLetter = 'f';
            case 7 -> returnedLetter = 'g';
            case 8 -> returnedLetter = 'h';
        }

        return returnedLetter;
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
    public String toString() {
        return "startPosition=" + numberToLetter(startPosition.getColumn()) + startPosition.getRow() +
                ", endPosition=" + numberToLetter(endPosition.getColumn()) + endPosition.getRow() +
                ", promotionPiece=" + promotionPiece + '\n';
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
