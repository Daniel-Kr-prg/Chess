package chess.model.figures;

import chess.model.Board;
import chess.model.Colors;
import chess.model.Move;
import chess.model.Vector;

import java.util.ArrayList;

public class King extends Figure {

    public boolean firstMove = true;

    public King(Colors color, Board board, Vector position) {
        super(color, board, position);
        figureColorAndSymbol += "K";
        if (position.X == 4 && (position.Y == 0 || position.Y == board.size_Y - 1))
            firstMove = true;
    }

    @Override
    public void computeMoves(boolean Check) {
        if (!moves.isEmpty()) {
            moves.clear();
        }
        if (firstMove && (position.X == 4)) {
            getLeftCastling(Check);
            getRightCastling(Check);
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Vector current = new Vector(i + position.X,j + position.Y);
                // if vector is in range and it's not check to this king
                if (Vector.inRange(current, -1, 8)
                        && (board.getCellStatus(current) != color.ordinal())) {
                    if (Check && (board.possibleCheck(new Move(position, current), color)))
                        continue;
                    moves.add(current);
                }
            }
        }

    }

    @Override
    public void setPosition(Vector destination) {
        position = destination;
            if (color == Colors.White) {
                board.whiteKingPosition = destination;
            }
            else {
                board.blackKingPosition = destination;
            }
    }

    @Override
    public ArrayList<Vector> getHits()
    {
        return moves;
    }

    @Override
    public boolean move(Vector destination) {
        if (Vector.contains(moves, destination) && (board.getCellStatus(destination) != color.ordinal())) {
            Vector old_pos = position;
            setPosition(destination);
            Move kingMove = new Move(old_pos, destination);

            if (firstMove) {
                if (destination.X == 6) {
                    Move rookMove = new Move(new Vector(old_pos.X + 3, old_pos.Y), new Vector(old_pos.X + 1, old_pos.Y));
                    //move 2 figures on the board
                    board.moveFigure(rookMove, false, false);
                    board.moveFigure(kingMove, false, false);
                    //commit the move and show it
                    board.commit(kingMove, color, true, "O-O");
                    board.showCastling(kingMove, rookMove, color);
                }
                else if (destination.X == 2) {
                    Move rookMove = new Move(new Vector(old_pos.X - 4, old_pos.Y), new Vector(old_pos.X - 1, old_pos.Y));
                    //move 2 figures on the board
                    board.moveFigure(rookMove, false, false);
                    board.moveFigure(kingMove, false, false);
                    //commit the move and show it
                    board.commit(kingMove, color, true, "O-O-O");
                    board.showCastling(kingMove, rookMove, color);
                }
                else
                    board.moveFigure(kingMove, true, true);
            }
            else
                board.moveFigure(kingMove, true, true);

            System.out.println("moving to " + position.X + " " + position.Y);
            firstMove = false;
            return true;
        }
        else {
            return false;
        }
    }

    private void getLeftCastling(boolean Check) {
        Vector rookDest = new Vector(position.X - 4, position.Y);
        Figure rook = board.getFigure(rookDest);
        if (rook != null) {
            if ((board.getFigure(new Vector(position.X - 3, position.Y)) == null)
                    && (board.getFigure(new Vector(position.X - 2, position.Y)) == null)
                    && (board.getFigure(new Vector(position.X - 1, position.Y)) == null)) {
                Move rookMove = new Move(rookDest, new Vector(position.X - 1, position.Y));
                board.moveFigure(rookMove,  false, false);
                if (Check && !board.possibleCheck(new Move(position, new Vector(position.X - 2, position.Y)), color))
                    moves.add(new Vector(position.X - 2, position.Y));
                board.undoMoveFigure(rookMove, false);
            }
        }

    }
    private void getRightCastling(boolean Check) {
        Vector rookDest = new Vector(position.X + 3, position.Y);
        Figure rook = board.getFigure(rookDest);
        if (rook != null) {
            Vector rookCastlingPos = new Vector(position.X + 2, position.Y);
            if ((board.getFigure(rookCastlingPos) == null)
                    && (board.getFigure(new Vector(position.X + 1, position.Y)) == null)) {
                Move rookMove = new Move(rookDest, new Vector(position.X + 1, position.Y));
                board.moveFigure(rookMove, false, false);
                if (Check && !board.possibleCheck(new Move(position, rookCastlingPos), color))
                    moves.add(new Vector(position.X + 2, position.Y));
                board.undoMoveFigure(rookMove, false);
            }
        }
    }
}
