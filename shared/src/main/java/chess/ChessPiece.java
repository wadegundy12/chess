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
                System.out.println("Monday");
                break;
            case QUEEN:
                System.out.println("Tuesday");
                break;
            case BISHOP:
                return bishopMoves(board,myPosition);
            case KNIGHT:
                System.out.println("Thursday");
                break;
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

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        //up-right
        for(int i = myPosition.getRow(), j = myPosition.getColumn(); i <= 8 && j <= 8; i++, j++){
            if(board.getPiece(new ChessPosition(i,j)) == null){
                moves.add(new ChessMove(myPosition,new ChessPosition(i,j), null));
            }
        }

        //down-right
        for(int i = myPosition.getRow(), j = myPosition.getColumn(); i > 0 && j <= 8; i--, j++){
            if(board.getPiece(new ChessPosition(i,j)) == null){
                moves.add(new ChessMove(myPosition,new ChessPosition(i,j), null));
            }
        }

        //up-left
        for(int i = myPosition.getRow(), j = myPosition.getColumn(); i <= 8 && j > 0 ; i++, j--){
            if(board.getPiece(new ChessPosition(i,j)) == null){
                moves.add(new ChessMove(myPosition,new ChessPosition(i,j), null));
            }
        }

        //down-left
        for(int i = myPosition.getRow(), j = myPosition.getColumn(); i > 0 && j > 0; i--, j--){
            if(board.getPiece(new ChessPosition(i,j)) == null){
                moves.add(new ChessMove(myPosition,new ChessPosition(i,j), null));
            }
        }

        return moves;
    }
}
