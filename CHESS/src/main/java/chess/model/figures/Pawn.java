package chess.model.figures;

import chess.model.Colors;
import chess.model.Move;
import chess.model.Vector;
import chess.model.Board;

import java.util.ArrayList;

public class Pawn extends Figure{

    public boolean firstMove = false;

    private Vector defaultMove = new Vector(0,1);

    public Pawn(Colors color, Board board, Vector position) {
        super(color, board, position);
        if (color == Colors.Black)
            defaultMove.Y = -1;
        figureColorAndSymbol += "P";
        if ((color == Colors.White && position.Y == 1) || (color == Colors.Black && position.Y == 6))
            firstMove = true;
    }

    @Override
    public void computeMoves(boolean Check) {
        if (!moves.isEmpty()) {
            moves.clear();
        }
        addMove(0, Check);
        if (firstMove && Vector.contains(moves, new Vector(position.X, position.Y + defaultMove.Y))) {
            Vector current = new Vector(position.X, position.Y + defaultMove.Y * 2);
            int cellStatus = board.getCellStatus(current);
            if (cellStatus == -1) {
                if (Check  && board.possibleCheck(new Move(position, current), color))
                    return;
                moves.add(current);
            }
        }
        if (position.X > 0) {
            addMove(-1, Check);
        }
        if (position.X < 7) {
            addMove(1, Check);
        }
    }

    private void addMove(int X, boolean Check) {
        Vector current = new Vector(position.X + X, position.Y + defaultMove.Y);
        int cellStatus = board.getCellStatus(current);
        int status_to_check = X == 0 ? -1 : Math.abs(color.ordinal() - 1);
        if (cellStatus == status_to_check) {
            if (Check && board.possibleCheck(new Move(position, current), color))
                return;
            moves.add(current);
        }
    }

    @Override
    public ArrayList<Vector> getHits() {
        ArrayList<Vector> hits = new ArrayList<>();
        if (position.X > 0) {
            hits.add(new Vector(position.X - 1, position.Y + defaultMove.Y));
        }
        if (position.X < board.size_Y) {
            hits.add(new Vector(position.X + 1, position.Y + defaultMove.Y));
        }

        return hits;
    }

    @Override
    public boolean move(Vector destination) {
        if (Vector.contains(moves, destination) && (board.getCellStatus(destination) != color.ordinal())) {
            Vector old_pos = position;
            position = destination;

            if (position.Y == 0 || position.Y == board.size_Y-1) {
                board.showPromote(this, new Move(old_pos, position));
            }
            else {
                board.moveFigure(new Move(old_pos, destination), true, true);
            }

            System.out.println("moving to " + position.X + " " + position.Y);
            firstMove = false;
            return true;
        }
        else {
            return false;
        }
    }
}
