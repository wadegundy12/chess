package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable{

    private TeamColor turn;
    private ChessBoard board;





    public ChessGame() {
        turn =TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();

    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null){
            return null;
        }

        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board,startPosition);
        if (!moves.isEmpty()){
            Iterator<ChessMove> iterator = moves.iterator();
            while(iterator.hasNext()){
                ChessGame tempGame;
                try {
                    tempGame = this.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                ChessMove tempMove = iterator.next();
                ChessGame.TeamColor tempColor = board.getPiece(startPosition).getTeamColor();
                tempGame.getBoard().movePiece(tempMove);
                if(tempGame.isInCheck(tempColor)){
                    iterator.remove();
                }
            }
        }

        return moves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessGame.TeamColor pieceColor = board.getPiece(move.getStartPosition()).getTeamColor();
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        if (pieceColor == turn && possibleMoves.contains(move)){
            board.movePiece(move);
            if (move.getPromotionPiece() != null){
                board.addPiece(move.getEndPosition(),new ChessPiece(turn,move.getPromotionPiece()));
            }
        }
        else{
            throw new InvalidMoveException();
        }

        if(turn == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        }
        else{
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        Collection<ChessMove> moves = new ArrayList<>();
        boolean inCheck = false;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition newPosition = new ChessPosition(i,j);
                if (board.getPiece(newPosition) != null) {
                    ChessPiece tempPiece = new ChessPiece(board.getPiece(newPosition));
                    if (tempPiece.getPieceType() == ChessPiece.PieceType.KING && tempPiece.getTeamColor() == teamColor){
                        kingPosition =  new ChessPosition(i,j);
                    }
                    else if (tempPiece.getTeamColor() != teamColor){
                        moves.addAll(tempPiece.pieceMoves(board,newPosition));
                    }
                }
            }
        }

        if(kingPosition == null){
            return false;
        }

        if (!moves.isEmpty()) {
            for (ChessMove tempMove : moves){
                if (tempMove.getEndPosition().equals(kingPosition)){
                    inCheck = true;
                    break;
                }
            }
        }

        return inCheck;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){return false;}
        return isInStalemate(teamColor);

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition tempPosition = new ChessPosition(i,j);
                if(board.getPiece(tempPosition) != null && board.getPiece(tempPosition).getTeamColor() == teamColor){
                    moves.addAll(validMoves(tempPosition));
                }
            }
        }
        return moves.isEmpty();
    }





        /**
         * Sets this game's chessboard with a given board
         *
         * @param board the new board to use
         */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public ChessGame clone() throws CloneNotSupportedException{
        ChessGame chessGame2 = (ChessGame) super.clone();
        chessGame2.board = this.getBoard().clone();
        return chessGame2;
    }
}
