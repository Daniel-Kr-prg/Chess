package chess.model;

import chess.model.Colors;
import chess.model.Model;
import chess.model.Vector;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LogTest {
    @Test
    public void testMoves()
    {
        Model mod = new Model(null);
        mod.initDefaultGame();

        mod.selectCell(new Vector(1,1));
        mod.selectCell(new Vector(1,3));

        String str = mod.getBoard().logger.getMoveString(0, Colors.White);
        int res = str.compareTo("b4");
        assertTrue("Wrong pawn move", res == 0);

        mod.selectCell(new Vector(1,7));
        mod.selectCell(new Vector(0,5));

        str = mod.getBoard().logger.getMoveString(0, Colors.Black);
        res = str.compareTo("Na6");
        System.out.println();
        assertTrue("Wrong knight move", res == 0);

        mod.selectCell(new Vector(2,1));
        mod.selectCell(new Vector(2,2));

        str = mod.getBoard().logger.getMoveString(1, Colors.White);
        res = str.compareTo("c3");
        System.out.println(str);
        assertTrue("Wrong white pawn move", res == 0);

        mod.selectCell(new Vector(0,5));
        mod.selectCell(new Vector(1,3));

        str = mod.getBoard().logger.getMoveString(1, Colors.Black);
        res = str.compareTo("Nxb4");
        System.out.println(str);
        assertTrue("Wrong knight killing pawn move", res == 0);

        mod.selectCell(new Vector(3,0));
        mod.selectCell(new Vector(2,1));

        str = mod.getBoard().logger.getMoveString(2, Colors.White);
        res = str.compareTo("Qc2");
        System.out.println(str);
        assertTrue("Wrong queen", res == 0);

        mod.selectCell(new Vector(1,3));
        mod.selectCell(new Vector(2,1));

        str = mod.getBoard().logger.getMoveString(2, Colors.Black);
        res = str.compareTo("Nxc2+");
        System.out.println(str);
        assertTrue("Wrong check move", res == 0);
    }

    @Test
    public void testPromotion()
    {
        Model mod = new Model(null);
        mod.initDefaultGame();

        // WHITE
        mod.selectCell(new Vector(1,1));
        mod.selectCell(new Vector(1,3));

        // BLACK
        mod.selectCell(new Vector(0,6));
        mod.selectCell(new Vector(0,4));

        // WHITE
        mod.selectCell(new Vector(1,3));
        mod.selectCell(new Vector(0,4));

        // BLACK
        mod.selectCell(new Vector(5,6));
        mod.selectCell(new Vector(5,5));

        // WHITE
        mod.selectCell(new Vector(0,4));
        mod.selectCell(new Vector(0,5));

        // BLACK
        mod.selectCell(new Vector(5,5));
        mod.selectCell(new Vector(5,4));

        // WHITE
        mod.selectCell(new Vector(0,5));
        mod.selectCell(new Vector(0,6));

        // BLACK
        mod.selectCell(new Vector(5,4));
        mod.selectCell(new Vector(5,3));

        // WHITE
        mod.selectCell(new Vector(0,6));
        mod.selectCell(new Vector(1,7));

        String str = mod.getBoard().logger.getMoveString(4, Colors.White);
        int res = str.compareTo("xb8=Q");
        System.out.println(str);
        assertTrue("Wrong promotion move", res == 0);
    }
}
