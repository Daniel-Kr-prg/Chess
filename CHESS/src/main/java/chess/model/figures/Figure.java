package chess.model.figures;

import chess.model.*;

import java.util.ArrayList;

public abstract class Figure implements IFigure{
    protected Colors color;
    protected Board board;
    protected Vector position;

    /**
     * symbol for printing to the console
     */
    public String figureColorAndSymbol;


    protected ArrayList<Vector> moves;

    public Figure() {

    }
    public Figure(Colors color, Board board, Vector position) {
        this.color = color;
        this.board = board;
        this.position = position;
        figureColorAndSymbol = color == Colors.White ? "w" : "b";
        moves = new ArrayList<Vector>();
    }
    /**
     * @param Check do this figure should control the check to its king?
     */
    public void computeMoves(boolean Check){

    }
    /**
     * @return the list of all possible attacks by this figure
     */
    public ArrayList<Vector> getHits() { return null; }

    /**
     * @return All the possible moves
     */
    public ArrayList<Vector> getMoves() {
        return moves;
    }

    /**
     * try to move on this cell
     * @param destination cell from the board
     */
    public boolean move(Vector destination) {
        if (Vector.contains(moves, destination) && (board.getCellStatus(destination) != color.ordinal())) {
            board.moveFigure(new Move(position, destination), true, true);
            Model.consoleLogger.info("moving to " + position.X + " " + position.Y);
            return true;
        }
        else {
            Model.consoleLogger.severe("THIS MOVE IS NOT POSSIBLE : " + destination.X + " " + destination.Y);
            return false;
        }
    }

    /**
     * get the color of the figure
     */
    public Colors getColor()
    {
        return color;
    }
    /**
     * get the position of the figure
     */
    public Vector getPosition()
    {
        return position;
    }
    /**
     * set position of the figure
     */
    public void setPosition(Vector pos) { position = pos;}

    protected void computeMovesOnVector(Vector direction, boolean Check) {
        Vector current = position.add(direction);
        while (true) {
            if (Vector.inRange(current, -1, board.size_X) &&
                    (board.getCellStatus(current) != color.ordinal())) {
                if (Check && (board.possibleCheck(new Move(position, current), color)))
                    return;
                moves.add(current);
                if (board.getCellStatus(current) == Math.abs(color.ordinal() - 1)) {
                    return;
                }
                current = current.add(direction);
            }
            else
                return;
        }
    }
}
