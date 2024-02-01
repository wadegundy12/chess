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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        switch (type) {
            case KING:
                return kingMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board,myPosition);
            case BISHOP:
                return bishopMoves(board,myPosition);
            case KNIGHT:
                return knightMoves(board,myPosition);
            case ROOK:
                return rookMoves(board,myPosition);
            case PAWN:
                if (board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE){
                    return whitePawnMoves(board,myPosition);
                }
                return blackPawnMoves(board,myPosition);
            default:

        }
        return null;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        //up-right
        if(myPosition.getRow() != 8 && myPosition.getColumn()!= 8) {
            for(int i = myPosition.getRow() + 1, j = myPosition.getColumn() + 1; i <= 8 && j <= 8; i++, j++) {
                ChessPosition newPosition = new ChessPosition(i,j);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else{
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }

        //down-right
        if(myPosition.getRow() != 0 && myPosition.getColumn()!= 8) {
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() + 1; i > 0 && j <= 8; i--, j++) {
                ChessPosition newPosition = new ChessPosition(i,j);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }

        //up-left
        if(myPosition.getRow() != 8 && myPosition.getColumn()!= 0) {
            for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() - 1; i <= 8 && j > 0; i++, j--) {
                ChessPosition newPosition = new ChessPosition(i,j);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }

        //down-left
        if(myPosition.getRow() != 0 && myPosition.getColumn()!= 0) {
            for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() - 1; i > 0 && j > 0; i--, j--) {
                ChessPosition newPosition = new ChessPosition(i,j);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }

        return moves;
    }


    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int rowLowerLim = 1, colLowerLim = 1, rowUpperLim = 3, colUpperLim = 3;

        if(myPosition.getRow() == 0){
            rowLowerLim = 0;
        }
        if(myPosition.getRow() == 8){
            rowUpperLim = 2;
        }
        if(myPosition.getColumn() == 0){
            colLowerLim = 0;
        }
        if(myPosition.getColumn() == 8){
            colUpperLim = 2;
        }

        for(int i = 0; i < rowUpperLim; i++){
            for (int j = 0; j < colUpperLim; j++){
                ChessPosition newPosition = new ChessPosition(myPosition.getRow() + i - rowLowerLim, myPosition.getColumn() + j - colLowerLim);
                if (board.getPiece(newPosition) == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if(newPosition != myPosition){
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }



        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition newPosition;
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();


        boolean u8 = (myPosition.getRow() < 8);
        boolean u7 = (myPosition.getRow() < 7);
        boolean o1 = (myPosition.getRow() > 1);
        boolean o2 = (myPosition.getRow() > 2);
        boolean l8 = (myPosition.getColumn() < 8);
        boolean l7 = (myPosition.getColumn() < 7);
        boolean r1 = (myPosition.getColumn() > 1);
        boolean r2 = (myPosition.getColumn() > 2);


        if(u8){
              if(l7){
                  newPosition = new ChessPosition(myRow + 1, myCol + 2);
                  if (board.getPiece(newPosition) == null) {
                      moves.add(new ChessMove(myPosition, newPosition, null));
                  }
                  else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                      moves.add(new ChessMove(myPosition, newPosition, null));

                  }
              }
            if(r2){
                newPosition = new ChessPosition(myRow + 1, myCol - 2);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                    moves.add(new ChessMove(myPosition, newPosition, null));

                }
            }

        }

        if(u7){
            if(l8){
                newPosition = new ChessPosition(myRow + 2, myCol + 1);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                    moves.add(new ChessMove(myPosition, newPosition, null));

                }
            }
            if(r1){
                newPosition = new ChessPosition(myRow + 2, myCol - 1);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                    moves.add(new ChessMove(myPosition, newPosition, null));

                }
            }

        }

        if(o1){
            if(l7){
                newPosition = new ChessPosition(myRow - 1, myCol + 2);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                    moves.add(new ChessMove(myPosition, newPosition, null));

                }
            }
            if(r2){
                newPosition = new ChessPosition(myRow - 1, myCol - 2);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                    moves.add(new ChessMove(myPosition, newPosition, null));

                }
            }

        }

        if(o2){
            if(l8){
                newPosition = new ChessPosition(myRow - 2, myCol + 1);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                    moves.add(new ChessMove(myPosition, newPosition, null));

                }
            }
            if(r1){
                newPosition = new ChessPosition(myRow - 2, myCol - 1);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                    moves.add(new ChessMove(myPosition, newPosition, null));

                }
            }

        }

        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        if (myPosition.getRow() != 8) {
            int col = myPosition.getColumn();
            for(int i = myPosition.getRow() + 1; i <= 8; i++){
                ChessPosition newPosition = new ChessPosition(i,col);
                if (board.getPiece(newPosition) == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else{
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }

            }
        }

        if (myPosition.getRow() != 1) {
            int col = myPosition.getColumn();
            for(int i = myPosition.getRow() - 1; i >= 1; i--){
                ChessPosition newPosition = new ChessPosition(i,col);
                if (board.getPiece(newPosition) == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else{
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }

            }
        }

        if (myPosition.getColumn() != 8) {
            int row = myPosition.getRow();
            for(int i = myPosition.getColumn() + 1; i <= 8; i++){
                ChessPosition newPosition = new ChessPosition(row,i);
                if (board.getPiece(newPosition) == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else{
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }

            }
        }

        if (myPosition.getColumn() != 1) {
            int row = myPosition.getRow();
            for(int i = myPosition.getColumn() -1; i >= 1; i--){
                ChessPosition newPosition = new ChessPosition(row,i);
                if (board.getPiece(newPosition) == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else{
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }

            }
        }


        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(rookMoves(board,myPosition));
        moves.addAll(bishopMoves(board,myPosition));
        return moves;
    }

    private Collection<ChessMove> whitePawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition newPosition;

        if (myPosition.getRow() == 2) {
            newPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
            if ((board.getPiece(newPosition) == null)  && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null)){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }


            if (myPosition.getRow() != 7){
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if (board.getPiece(newPosition) == null){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            if(myPosition.getColumn() != 8) {
                newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.BLACK)) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

            if(myPosition.getColumn() != 1) {
                newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.BLACK)) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

        }

        else {
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if (board.getPiece(newPosition) == null){
                moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            }
            if(myPosition.getColumn() != 8) {
                newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.BLACK)) {
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                }
            }

            if(myPosition.getColumn() != 1) {
                newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.BLACK)) {
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> blackPawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition newPosition;


        if (myPosition.getRow() == 7) {
            newPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
            if ((board.getPiece(newPosition) == null)  && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null)){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        if (myPosition.getRow() != 2){
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if (board.getPiece(newPosition) == null){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            if(myPosition.getColumn() != 8) {
                newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.WHITE)) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

            if(myPosition.getColumn() != 1) {
                newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.WHITE)) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

        }

        else {
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if (board.getPiece(newPosition) == null){
                moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            }
            if(myPosition.getColumn() != 8) {
                newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.WHITE)) {
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                }
            }

            if(myPosition.getColumn() != 1) {
                newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                if ((board.getPiece(newPosition) != null) && (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.WHITE)) {
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                }
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
