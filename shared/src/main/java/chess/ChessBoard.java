package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void removePiece(ChessPosition position) {
        squares[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        // find the chess piece at these coordinates
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // empty all squares
        for(int x = 1; x < 9; x++) {
            for(int y = 1; y < 9; y++) {
                squares[x-1][y-1] = null;
            }
        }

        // white layout
        addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        for(int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // black layout
        addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        for(int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    /**
     * this == o checks to see if the objects are the same memory location
     * o == null checks to see if what is being compared is not an object
     * getClass() != o.getClass() checks to see if the classes being compared are different
     * ChessBoard that = (ChessBoard) o this converts the 'o' object defined as class "Object" to the ChessBoard class
     * !Objects.equals(a, b) is used because if we use the != operator, it's looking for the same memory location versus the same value at that memory location
     * @param o the object being compared against the current object
     * @return if the classes match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessBoard that = (ChessBoard) o;
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!Objects.equals(squares[i][j], that.squares[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    // returns a string when the class is called
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();

        // row
        for (int x = 8; x >= 1; x--) {
            // column
            returnString.append(x);
            for (int y = 1; y <= 8; y++) {
                ChessPiece piece = getPiece(new ChessPosition(x, y));
                if (piece == null) {
                    returnString.append(" -");
                } else {
                    var thisPieceType = getPiece(new ChessPosition(x, y)).getPieceType();
                    var thisPieceColor = getPiece(new ChessPosition(x, y)).getTeamColor();

                    if (thisPieceColor == ChessGame.TeamColor.WHITE) {
                        switch (thisPieceType) {
                            case ROOK -> returnString.append(" R");
                            case KNIGHT -> returnString.append(" N");
                            case BISHOP -> returnString.append(" B");
                            case KING -> returnString.append(" K");
                            case QUEEN -> returnString.append(" Q");
                            case PAWN -> returnString.append(" P");
                        }
                    } else {
                        switch (thisPieceType) {
                            case ROOK -> returnString.append(" r");
                            case KNIGHT -> returnString.append(" n");
                            case BISHOP -> returnString.append(" b");
                            case KING -> returnString.append(" k");
                            case QUEEN -> returnString.append(" q");
                            case PAWN -> returnString.append(" p");
                        }
                    }
                }
            }
            returnString.append("\n");
        }
        returnString.append("  a b c d e f g h");

        return returnString.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
