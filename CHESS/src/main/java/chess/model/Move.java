package chess.model;

import chess.model.figures.Figure;
import chess.model.figures.Figures;

import static java.lang.Character.isUpperCase;

public class Move {
    public Vector from;
    public Vector to;
    public Figure killed;
    private String log = "";
    public Colors color;
    public boolean checkmate = false;
    public boolean check = false;
    public boolean promoting = false;
    public Figures promotingTo;

    /**
     * initialize the move
     */
    public Move(Vector from, Vector to)
    {
        this.from = from;
        this.to = to;
    }
    /**
     * remove figure's symbol if it exists
     */
    public void removeFigureSymbol()
    {
        if (isUpperCase(log.charAt(0)))
            log = log.substring(1);
    }
    /**
     * change the log to some text
     */
    public void customLog(String log)
    {
        this.log = log;
    }
    /**
     * add some text to the log
     */
    public void addToLog(String log)
    {
        if (log.length() > 0 && log.charAt(0) == '=')
            promoting = true;
        this.log += log;
    }
    /**
     * returns the log
     */
    public String getLog()
    {
        return log;
    }


}
