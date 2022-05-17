package chess.model.figures;

import chess.model.Board;
import chess.model.Colors;
import chess.model.Vector;

import java.util.ArrayList;

public class Bishop extends Figure{
    public Bishop(Colors color, Board board, Vector position) {
        super(color, board, position);
        figureColorAndSymbol += "B";
    }

    @Override
    public void computeMoves(boolean Check) {
        if (!moves.isEmpty()) {
            moves.clear();
        }
        // Get moves on vectors
        computeMovesOnVector(new Vector(1,1), Check);
        computeMovesOnVector(new Vector(1,-1), Check);
        computeMovesOnVector(new Vector(-1,-1), Check);
        computeMovesOnVector(new Vector(-1,1), Check);
    }

    @Override
    public ArrayList<Vector> getHits()
    {
        return moves;
    }
}
