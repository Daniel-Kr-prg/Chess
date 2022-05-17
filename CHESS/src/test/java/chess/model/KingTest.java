package chess.model;

import chess.model.Colors;
import chess.model.GameState;
import chess.model.Vector;
import chess.model.figures.Figures;
import chess.model.Model;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class KingTest {
    @Test
    public void testKingMoves() {
        Model mod = new Model(null);
        mod.getBoard().spawnFigure(Figures.Queen, Colors.White, new Vector(0,2));
        mod.getBoard().spawnFigure(Figures.Queen, Colors.Black, new Vector(0,6));
        mod.getBoard().spawnFigure(Figures.Rook, Colors.Black, new Vector(4,1));
        mod.getBoard().spawnFigure(Figures.Rook, Colors.Black, new Vector(5,1));
        mod.getBoard().spawnFigure(Figures.King, Colors.White, new Vector(0,0));


        mod.getBoard().printBoard();

        mod.startCustomGame(false);


        mod.selectCell(new Vector(0,6));
        mod.selectCell(new Vector(0,2));

        mod.getBoard().printBoard();

        mod.selectCell(new Vector(0,0));
        mod.selectCell(new Vector(1,0));

        mod.getBoard().printBoard();

        mod.selectCell(new Vector(4,1));
        mod.selectCell(new Vector(4,0));

        mod.getBoard().printBoard();


        assertSame("Wrong moves list", mod.getGameState(), GameState.Finished);
    }
}
