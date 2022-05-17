package chess.model.figures;

import chess.model.Board;
import chess.model.Colors;
import chess.model.Move;
import chess.model.Vector;

import java.util.ArrayList;

public class Knight extends Figure
{
    public Knight(Colors color, Board board, Vector position) {
        super(color, board, position);
        figureColorAndSymbol += "N";
    }

    @Override
    public void computeMoves(boolean Check) {
        if (!moves.isEmpty()) {
            moves.clear();
        }

        addMove(new Vector(2,1), Check);
        addMove(new Vector(2,-1), Check);
        addMove(new Vector(-2,1), Check);
        addMove(new Vector(-2,-1), Check);
        addMove(new Vector(1,2), Check);
        addMove(new Vector(1,-2), Check);
        addMove(new Vector(-1,2), Check);
        addMove(new Vector(-1,-2), Check);
    }

    private void addMove(Vector move, boolean Check) {
        Vector destination = position.add(move);
        if (Vector.inRange(destination, -1, board.size_Y) &&
                (board.getCellStatus(destination) != color.ordinal())) {
            if (Check && board.possibleCheck(new Move(position, destination), color))
                return;
            moves.add(destination);
        }
    }

    @Override
    public ArrayList<Vector> getHits()
    {
        return moves;
    }
}
