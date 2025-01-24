package chess;

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

    final int UP = 1;
    final int DOWN = -1;
    final int RIGHT = 1;
    final int LEFT = -1;
    final int NOCHANGE = 0;

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

    /**
     * Finds valid moves in the given direction and returns them
     * This takes into account the color and taking of enemy pieces
     * @param board the board
     * @param myPosition initial position
     * @param rowChange the direction the piece will move diagonally (-1 or +1)
     * @param colChange the direction the piece will move horizontally
     * @return returns all valid moves
     */
    private Collection<ChessMove> checkAndReturnMoves(ChessBoard board, ChessPosition myPosition, int rowChange, int colChange) {

        var validMoves = new ArrayList<ChessMove>();
        var checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        while (true) {
            int newRow = checkingPosition.getRow() + rowChange;
            int newCol = checkingPosition.getColumn() + colChange;

            if (!(newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8)) {
                break;
            }

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

    /**
     * determines if a move is in bounds or not
     * @param myPosition initial position
     * @param rowChange UP/DOWN/NOCHANGE
     * @param colChange LEFT/RIGHT/NOCHANGE
     * @return returns true if inbounds and false if not
     */
    private boolean isInBounds(ChessPosition myPosition, int rowChange, int colChange) {
        int newRow = myPosition.getRow() + rowChange;
        int newCol = myPosition.getColumn() + colChange;
        return newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8;
    }

    /**
     * calculates the moves a pawn can do excluding en peasant
     * @param board game board
     * @param myPosition initial pawn position
     * @return returns valid moves (either one or two ahead)
     */
    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        var validMoves = new ArrayList<ChessMove>();
        var thisPieceColor = board.getPiece(myPosition).getTeamColor();
        var checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {

            if (isInBounds(myPosition, UP, NOCHANGE)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn());

                if (checkingPosition.getRow() == 8 && board.getPiece(checkingPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.ROOK));

                }
                else if (board.getPiece(checkingPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, null));

                }
            }

            if (isInBounds(myPosition, UP*2, NOCHANGE)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn());
                var checkingPositionDoubleMove = new ChessPosition(myPosition.getRow() + (UP * 2), myPosition.getColumn());

                if (myPosition.getRow() == 2 && board.getPiece(checkingPosition) == null && board.getPiece(checkingPositionDoubleMove) == null) {
                    validMoves.add(new ChessMove(myPosition, checkingPositionDoubleMove, null));
                }
            }

            if (isInBounds(myPosition, UP, RIGHT)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn() + RIGHT);

                if (
                        checkingPosition.getRow() == 8
                        && board.getPiece(checkingPosition) != null
                        && thisPieceColor != board.getPiece(checkingPosition).getTeamColor())
                {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.ROOK));

                }
                else if (board.getPiece(checkingPosition) != null && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, null));
                }
            }

            if (isInBounds(myPosition, UP, LEFT)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn() + LEFT);

                if (
                        checkingPosition.getRow() == 8
                                && board.getPiece(checkingPosition) != null
                                && thisPieceColor != board.getPiece(checkingPosition).getTeamColor())
                {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.ROOK));

                } else if (board.getPiece(checkingPosition) != null && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, null));
                }
            }
        }

        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {

            if (isInBounds(myPosition, DOWN, NOCHANGE)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn());

                if (checkingPosition.getRow() == 1 && board.getPiece(checkingPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.ROOK));

                } else if (board.getPiece(checkingPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, null));

                }
            }

            if (isInBounds(myPosition, DOWN*2, NOCHANGE)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn());
                var checkingPositionDoubleMove = new ChessPosition(myPosition.getRow() + (DOWN * 2), myPosition.getColumn());

                if (myPosition.getRow() == 7 && board.getPiece(checkingPosition) == null && board.getPiece(checkingPositionDoubleMove) == null) {
                    validMoves.add(new ChessMove(myPosition, checkingPositionDoubleMove, null));
                }
            }

            if (isInBounds(myPosition, DOWN, RIGHT)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn() + RIGHT);

                if (
                        checkingPosition.getRow() == 1
                                && board.getPiece(checkingPosition) != null
                                && thisPieceColor != board.getPiece(checkingPosition).getTeamColor())
                {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.ROOK));

                } else if (board.getPiece(checkingPosition) != null && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, null));
                }
            }

            if (isInBounds(myPosition, DOWN, LEFT)) {
                checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn() + LEFT);

                if (
                        checkingPosition.getRow() == 1
                                && board.getPiece(checkingPosition) != null
                                && thisPieceColor != board.getPiece(checkingPosition).getTeamColor())
                {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, checkingPosition, PieceType.ROOK));

                } else if (board.getPiece(checkingPosition) != null && thisPieceColor != board.getPiece(checkingPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, checkingPosition, null));
                }
            }
        }

        return validMoves;
    }

    private Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        var validMoves = new ArrayList<ChessMove>();
        var thisPieceColor = board.getPiece(myPosition).getTeamColor();
        var checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        if (isInBounds(myPosition, UP, NOCHANGE)) {
            checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn());

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
        }

        if (isInBounds(myPosition, UP, RIGHT)) {
            checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn() + RIGHT);

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
        }

        if (isInBounds(myPosition, UP, LEFT)) {
            checkingPosition = new ChessPosition(myPosition.getRow() + UP, myPosition.getColumn() + LEFT);

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
        }

        if (isInBounds(myPosition, DOWN, NOCHANGE)) {
            checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn());

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
        }

        if (isInBounds(myPosition, DOWN, RIGHT)) {
            checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn() + RIGHT);

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
        }

        if (isInBounds(myPosition, DOWN, LEFT)) {
            checkingPosition = new ChessPosition(myPosition.getRow() + DOWN, myPosition.getColumn() + LEFT);

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
        }

        if (isInBounds(myPosition, NOCHANGE, RIGHT)) {
            checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + RIGHT);

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
        }

        if (isInBounds(myPosition, NOCHANGE, LEFT)) {
            checkingPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + LEFT);

            if ((board.getPiece(checkingPosition) == null) || (board.getPiece(checkingPosition).getTeamColor() != thisPieceColor)) {
                validMoves.add(new ChessMove(myPosition, checkingPosition, null));
            }
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

        if (selectedPieceType.type == PieceType.PAWN) {
            validMoves.addAll(calculatePawnMoves(board, myPosition));
        }

        if (selectedPieceType.type == PieceType.KING) {
            validMoves.addAll(calculateKingMoves(board, myPosition));
        }

        /*
         * ADD PAWN, KNIGHT, & KING (ONLY TESTS LEFT)
         * KNIGHT:
         * - Can only do an L shaped move in UP DOWN RIGHT LEFT and then facing two different ways in each direction
         * PAWN:
         * - Can move 2 tiles first turn
         * - Only move 1 tile forward thereafter
         * - Once reaches row 1 or 8, it will become a different piece
         * KING:
         * - Can only move 1 but in any direction
         */

        return validMoves;
    }
}
