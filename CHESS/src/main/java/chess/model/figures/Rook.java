package chess.model.figures;

import chess.model.Vector;
import chess.model.Board;
import chess.model.Colors;

import java.util.ArrayList;

public class Rook extends Figure{
    public Rook(Colors color, Board board, Vector position) {
        super(color, board, position);
        figureColorAndSymbol += "R";
    }

    @Override
    public void computeMoves(boolean Check) {
        if (!moves.isEmpty()) {
            moves.clear();
        }
        computeMovesOnVector(new Vector(0,1), Check);
        computeMovesOnVector(new Vector(1,0), Check);
        computeMovesOnVector(new Vector(0,-1), Check);
        computeMovesOnVector(new Vector(-1,0), Check);

    }

    @Override
    public ArrayList<Vector> getHits()
    {
        return moves;
    }
}
