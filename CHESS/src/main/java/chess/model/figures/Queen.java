package chess.model.figures;

import chess.model.Board;
import chess.model.Colors;
import chess.model.Vector;

import java.util.ArrayList;

public class Queen extends Figure {
    public Queen(Colors color, Board board, Vector position) {
        super(color, board, position);
        figureColorAndSymbol += "Q";

    }

    @Override
    public void computeMoves(boolean Check) {
        if (!moves.isEmpty()) {
            moves.clear();
        }
        computeMovesOnVector(new Vector(0,1), Check);
        computeMovesOnVector(new Vector(1,1), Check);
        computeMovesOnVector(new Vector(1,0), Check);
        computeMovesOnVector(new Vector(1,-1), Check);
        computeMovesOnVector(new Vector(0,-1), Check);
        computeMovesOnVector(new Vector(-1,-1), Check);
        computeMovesOnVector(new Vector(-1,0), Check);
        computeMovesOnVector(new Vector(-1,1), Check);

    }

    @Override
    public ArrayList<Vector> getHits()
    {
        return moves;
    }
}
