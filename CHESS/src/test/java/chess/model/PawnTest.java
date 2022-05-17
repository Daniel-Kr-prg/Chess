package chess.model;

import chess.model.figures.Figure;
import chess.model.figures.Figures;
import chess.model.Model;
import org.junit.*;
import static org.junit.Assert.*;
import chess.model.Colors;
import chess.model.Vector;

import java.util.ArrayList;

public class PawnTest {
    @Test
    public void testSpawnWhitePawn() {
        Model mod = new Model(null);
        mod.getBoard().spawnFigure(Figures.Pawn, Colors.White, new Vector(0,0));
        Figure p = mod.getBoard().getFigure(new Vector(0,0));
        assertNotNull("Missed Pawn :(", p);
    }
    @Test
    public void testPawnMoves() {
        Model mod = new Model(null);

        mod.getBoard().spawnFigure(Figures.Pawn, Colors.White, new Vector(0,0));
        mod.getBoard().spawnFigure(Figures.Pawn, Colors.Black, new Vector(1,1));
        mod.getBoard().spawnFigure(Figures.Pawn, Colors.Black, new Vector(0,1));


        mod.getBoard().getFigure(new Vector(0,0)).computeMoves(false);
        ArrayList<Vector> moves = mod.getBoard().getFigure(new Vector(0,0)).getMoves();

        ArrayList<Vector> expects = new ArrayList<Vector>();
        expects.add(new Vector(1,1));

        boolean result = moves.size() == 1;

        System.out.println("MOVES: ");
        for (int i = 0; i < moves.size(); i++)
        {
            System.out.println(moves.get(i).X + " " + moves.get(i).Y);
            if (moves.get(i).Y != expects.get(i).Y || moves.get(i).X != expects.get(i).X)
                result = false;
        }
        mod.getBoard().printBoard();

        mod.selectCell(new Vector(0,0));
        mod.selectCell(new Vector(1,1));

        mod.getBoard().printBoard();

        assertTrue("Wrong moves list", result);
    }

    @Test
    public void testPawnUpdate()
    {
        Model mod = new Model(null);
        mod.initDefaultGame();
        mod.getBoard().spawnFigure(Figures.Pawn, Colors.White, new Vector(1,6));
        mod.getBoard().printBoard();
        mod.selectCell(new Vector(1,6));
        mod.selectCell(new Vector(1,7));
        mod.getBoard().printBoard();
    }
}