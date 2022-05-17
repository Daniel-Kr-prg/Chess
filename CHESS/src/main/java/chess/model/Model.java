package chess.model;

import chess.model.figures.Figures;
import chess.view.Window;

import java.util.List;
import java.util.Random;

import java.util.logging.*;

public class Model {
    private Window view;

    private Board board;

    public static Logger consoleLogger = Logger.getLogger(Model.class.getName());

    /**
     * whose turn is it
     */
    public boolean whiteTurn;

    private boolean customSetUp = false;
    private Vector customCellSelected = new Vector(0,0);

    private PlayerTypes[] players = new PlayerTypes[2];

    private List<Move> move_log;
    private Log PGN_handler;

    private GameState state;
    private boolean vsHuman;
    /**
     * are you playing vs AI?
     */
    public boolean AImoves = false;

    private MyTimer timer;
    // for PGN format + marks on Board
    private String[] horizontal_labels = { "A", "B", "C", "D", "E", "F", "G", "H"};
    private String[] vertical_labels = { "1", "2", "3", "4", "5", "6", "7", "8"};

    // add shortnames for figures for PGN
    //
    //

    private boolean CellSelected;

    /**
     * initialize the model
     */
    public Model(Window view) {
        this.view = view;
        //Create the board and the timer
        board = new Board(8,8, this);
        timer = new MyTimer(this, 300);
        CellSelected = false;

        //Create the logger
        consoleLogger.info("Model is instantiated");
    }
    /**
     * initialize the default chess game
     */
    public void initDefaultGame() {
        // set all figures
        consoleLogger.info("Initializing default game");
        board.spawnDefaultFigures();

        whiteTurn = true;
        state = GameState.Playing;
        makeAIMove();
        consoleLogger.info("default game is ready");
    }
    /**
     * initialize the custom set up
     */
    public void startCustomGame(boolean whiteTurn)
    {
        this.whiteTurn = whiteTurn;
        customSetUp = false;
        state = GameState.Playing;
    }
    /**
     * spawn the figure on custom selected cell
     */
    public void spawnCustomFigure(Figures type, Colors color)
    {
        board.spawnFigure(type, color, customCellSelected);
    }
    /**
     * change gamemode
     * @param vs True if you're playing against human
     * @param youWhite True if you wanna play as white when playing with an AI
     */
    public void setVsHuman(boolean vs, boolean youWhite) {
        vsHuman = vs;
        if (vsHuman) {
                players[0] = PlayerTypes.Player;
                players[1] = PlayerTypes.Player;
        }
        else {
            if (youWhite) {
                players[0] = PlayerTypes.Player;
                players[1] = PlayerTypes.AI;
            }
            else
            {
                players[1] = PlayerTypes.Player;
                players[0] = PlayerTypes.AI;
            }
        }
        consoleLogger.info("Game type changed");
    }
    /**
     * restart the game
     * @param custom make custom setup
     */
    public void restart(boolean custom) {
        state = GameState.Finished;
        board = new Board(8,8,this);
        if (custom) {
            customSetUp = true;
            setVsHuman(true, true);
            consoleLogger.info("Custom setup chosen");
        }
        else {
            consoleLogger.info("Default game chosen");
            customSetUp = false;
            initDefaultGame();
        }
    }
    /**
     * returns the board
     */
    public Board getBoard()
    {
        return board;
    }
    /**
     * returns the view
     */
    public Window getView() {return view;}

    /**
     * select cell on the board
     * @param cell cell from the board
     */
    public void selectCell(Vector cell) {
        if (customSetUp)
        {
            customCellSelected = cell;
            consoleLogger.info("cell for custom setup was chosen");
            return;
        }

        if (state == GameState.Checkmate_By_Black ||
                state == GameState.Checkmate_By_White ||
                state == GameState.Finished) {
            consoleLogger.warning("Game is finished, any move is restricted");
            return;
        }

        if (view != null)
            view.setDefaultColors();
        else
            consoleLogger.warning("View is not instantiated. Visualizing is not possible");
        if (board == null)
            return;
        boolean myCell = board.isCellSelectable(cell, whiteTurn ? Colors.White : Colors.Black);
        if (myCell) {
            if (CellSelected && (board.getSelectedFigure().getPosition().compareTo(cell) == 0)) {
                CellSelected = false;
                return;
            }
            CellSelected = board.selectFigure(cell);
            if (CellSelected) {
                if (view != null)
                    view.movesToSelectedCells(board.getSelectedFigure().getMoves());
                else
                    consoleLogger.warning("View is not instantiated. Visualizing is not possible");
            }
            Model.consoleLogger.info("Cell was selected");
        }
        else {
            if (CellSelected) {
                Model.consoleLogger.info("Another cell was selected");
                CellSelected = false;


                if (board.getSelectedFigure().move(cell)) {
                    // SAVE MOVE TO LOG
                    whiteTurn = !whiteTurn;
                    if (whiteTurn) {
                        board.logger.nextMove();
                    }
                    if (view != null) {
                        view.showCurrentMoveInLog(board.logger.currentMove, whiteTurn ? Colors.White : Colors.Black);
                        view.turnSide();
                    }
                    else
                        consoleLogger.warning("View is not instantiated. Visualizing is not possible");
                    timer.stopIt();
                    if (state != GameState.Finished) {
                        while (timer.isAlive()) {}
                        timer = new MyTimer(this, 300);
                        timer.start();
                        makeAIMove();
                    }
                }
            }
        }
    }

    private void makeAIMove() {
        if (whiteTurn && players[0] == PlayerTypes.AI) {
            consoleLogger.info("AI moves as White");
            AImoves = true;
            AIMove();
        }
        else if ((!whiteTurn) && players[1] == PlayerTypes.AI) {
            consoleLogger.info("AI moves as Black");
            AImoves = true;
            AIMove();
        }
    }

    private void AIMove() {
        Random r = new Random();
        while ((state != GameState.Finished || state != GameState.Checkmate_By_White || state != GameState.Checkmate_By_Black) && !vsHuman) {
            while (!CellSelected && (state != GameState.Finished || state != GameState.Checkmate_By_White || state != GameState.Checkmate_By_Black) && !vsHuman) {
                selectCell(new Vector(r.nextInt(8), r.nextInt(8)));
            }
            if (board.getSelectedFigure().getMoves().size() > 0) {
                int move = r.nextInt(board.getSelectedFigure().getMoves().size());
                selectCell(board.getSelectedFigure().getMoves().get(move));
                AImoves = false;
                return;
            }
            else
                selectCell(new Vector(0,0));
        }
    }
    /**
     * surrender....
     */
    public void surrender() {
        if (whiteTurn)
        {
            consoleLogger.info("White surrendered");
            board.logger.surrender(Colors.White);
            notifyGameState(GameState.Checkmate_By_Black);
        }
        else
        {
            consoleLogger.info("Black surrendered");
            board.logger.surrender(Colors.Black);
            notifyGameState(GameState.Checkmate_By_White);
        }

        notifyGameState(GameState.Finished);
    }
    /**
     * change the game state
     */
    public void notifyGameState(GameState state) {
        this.state = state;
        if (state == GameState.Checkmate_By_Black || state == GameState.Checkmate_By_White) {
            if (view != null)
                view.showWin(state);
            else
                consoleLogger.warning("View is not instantiated. Visualizing is not possible");
            this.state = GameState.Finished;
        }
        else if (state == GameState.TimeLeft) {
            if (view != null)
                view.showWin(whiteTurn ? GameState.Checkmate_By_Black : GameState.Checkmate_By_White);
            else
                consoleLogger.warning("View is not instantiated. Visualizing is not possible");
            this.state = GameState.Finished;
        }
        consoleLogger.info("Game state changed");
    }
    /**
     * returns the game state
     */
    public GameState getGameState()
    {
        return state;
    }

    /**
     * undo the move
     */
    public void undo() {
        if (view != null)
            view.setDefaultColors();
        else
            consoleLogger.warning("View is not instantiated. Visualizing is not possible");
        if (state != GameState.Playing)
            return;
        if ((board.logger.currentMove == 0 && whiteTurn) ||
                (whiteTurn && board.logger.currentMove >= board.logger.blackMoves.size() + 1))
            return;

        whiteTurn = !whiteTurn;
        if (whiteTurn) {
            board.undoMoveFigure(board.logger.whiteMoves.get(board.logger.currentMove), true);
        }
        else {
            board.logger.currentMove--;
            board.undoMoveFigure(board.logger.blackMoves.get(board.logger.currentMove), true);
        }
        consoleLogger.info("Undo the move success");
        if (view != null) {
            view.showCurrentMoveInLog(board.logger.currentMove, whiteTurn ? Colors.White : Colors.Black);
            view.turnSide();
        }
        else
            consoleLogger.warning("View is not instantiated. Visualizing is not possible");
    }
    /**
     * redo the move
     */
    public void redo() {
        if (view != null)
            view.setDefaultColors();
        else
            consoleLogger.warning("View is not instantiated. Visualizing is not possible");
        if (state != GameState.Playing)
            return;
        if ((whiteTurn && board.logger.currentMove >= board.logger.whiteMoves.size()) ||
                (!whiteTurn && board.logger.currentMove >= board.logger.blackMoves.size()))
            return;
        if (whiteTurn) {
            board.moveFigure(board.logger.whiteMoves.get(board.logger.currentMove), true, false);
        }
        else {
            board.moveFigure(board.logger.blackMoves.get(board.logger.currentMove), true, false);
            board.logger.currentMove++;
        }

        whiteTurn = !whiteTurn;
        consoleLogger.info("Redo the move success");
        if (view != null) {
            view.showCurrentMoveInLog(board.logger.currentMove, whiteTurn ? Colors.White : Colors.Black);
            view.turnSide();
        }
        else
            consoleLogger.warning("View is not instantiated. Visualizing is not possible");
    }
}
