package chess;

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
                System.out.println("Tuesday");
                break;
            case BISHOP:
                return bishopMoves(board,myPosition);
            case KNIGHT:
                return knightMoves(board,myPosition);
            case ROOK:
                System.out.println("Friday");
                break;
            case PAWN:
                System.out.println("Saturday");
                break;
            default:
                System.out.println("How the heck did you get here");
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

}
