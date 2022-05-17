package chess.controller;

import chess.model.Board;
import chess.model.Colors;
import chess.model.Model;
import chess.model.Vector;
import chess.model.figures.Figures;
import chess.view.Window;

public class Controller {
    private Window view;
    private Model mod;

    /**
     * Create the controller
     *
     */
    public Controller() {

    }
    /**
     * @param vsHuman "true" if it's Player vs Player game
     * @param youWhite "true" if you play white if it's a game vs AI
     * @return nothing...
     */
    public void restart(boolean vsHuman, boolean youWhite) {
        mod.setVsHuman(vsHuman, youWhite);
        mod.restart(false);
    }

    /**
     * Set links to the window and model
     */
    public void setLinks(Window view, Model mod) {
        this.view = view;
        this.mod = mod;
    }
    /**
     * @param pos position on the board
     */
    public void selectCell(Vector pos)
    {
        mod.selectCell(pos);
    }
    /**
     * @param choice index of the chosen figure type
     */
    public void promoteSelected(int choice) {
        switch (choice) {
            case 0:
                mod.getBoard().updatePawn(Figures.Rook);
                break;
            case 1:
                mod.getBoard().updatePawn(Figures.Knight);
                break;
            case 2:
                mod.getBoard().updatePawn(Figures.Bishop);
                break;
            case 3:
                mod.getBoard().updatePawn(Figures.Queen);
                break;
            default:
                mod.getBoard().updatePawn(Figures.Queen);
                break;
        }

    }
    /**
     * just give up...
     */
    public void surrender()
    {
        mod.surrender();
    }
    /**
     * undo the last move
     */
    public void undo() {
        mod.undo();
    }
    /**
     * redo the last move
     */
    public void redo()
    {
        mod.redo();
    }

    /**
     * Save the game to PGN
     * @param filename path to the file
     */
    public void save(String filename) {
        if (filename != "")
            mod.getBoard().logger.save(filename);
    }
    /**
     * Load the game from PGN
     * @param filename path to the file
     */
    public void load(String filename) {
        if (filename != "")
        {
            mod.restart(false);
            mod.setVsHuman(true, true);
            mod.getBoard().logger.load(filename);
        }

    }

    /**
     * Spawn figure when in custom game mode
     * @param type index of the figure type
     * @param color color of the figure to spawn
     */
    public void spawn(int type, Colors color)
    {
        mod.spawnCustomFigure(Board.getFigureType(type), color);
    }
    /**
     * initialize custom game
     */
    public void customGame()
    {
        mod.restart(true);
        view.setNoPictures();
    }
    /**
     * start playing custom game
     */
    public void customGameStart(boolean whiteTurn)
    {
        mod.startCustomGame(whiteTurn);
    }
}
