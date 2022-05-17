package chess.model.figures;

import chess.model.Vector;

import java.util.ArrayList;

public interface IFigure {
    public void computeMoves(boolean Check);
    public ArrayList<Vector> getHits();
}
