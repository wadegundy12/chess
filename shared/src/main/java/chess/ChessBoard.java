package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];
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

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPosition newPosition;
        for(int i = 1; i <= 8; i++){
            newPosition = new ChessPosition(2,i);
            addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        for(int i = 1; i <= 8; i++){
            newPosition = new ChessPosition(7,i);
            addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        newPosition = new ChessPosition(1,1);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        newPosition = new ChessPosition(1,8);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        newPosition = new ChessPosition(1,2);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));

        newPosition = new ChessPosition(1,7);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));

        newPosition = new ChessPosition(1,3);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));

        newPosition = new ChessPosition(1,6);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));

        newPosition = new ChessPosition(1,4);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));

        newPosition = new ChessPosition(1,5);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        newPosition = new ChessPosition(8,1);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        newPosition = new ChessPosition(8,8);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        newPosition = new ChessPosition(8,2);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        newPosition = new ChessPosition(8,7);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        newPosition = new ChessPosition(8,3);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        newPosition = new ChessPosition(8,6);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        newPosition = new ChessPosition(8,4);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

        newPosition = new ChessPosition(8,5);
        addPiece(newPosition, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        for (int i = 0; i < squares.length; i++) {
            if (!Arrays.equals(squares[i], that.squares[i])){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
