package chess.model;

import chess.model.figures.*;

import java.util.ArrayList;

public class Board {
    private final Model model;

    private Figure[][] board;

    private Figure selectedFigure;

    /**
     * X-size of the board
     */
    public int size_X;
    /**
     * Y-size of the board
     */
    public int size_Y;

    /**
     * where is the white king???
     */
    public Vector whiteKingPosition;
    /**
     * where is the black king???
     */
    public Vector blackKingPosition;

    private boolean whiteKingExists = false;
    private boolean blackKingExists = false;

    private boolean customGame = false;

    /**
     * Logs all the moves in the game
     */
    public Log logger = new Log(this);

    /**
     * Create the Board object
     */
    public Board(int size_X, int size_Y, Model model) {
        this.model = model;
        board = new Figure[size_X][size_Y];

        this.size_X = size_X;
        this.size_Y = size_Y;
    }
    /**
     * Spawn a figure of some figure and color
     * @param figure The type of this figure
     * @param color The color of this figure
     * @param position The position of this figure
     */
    public void spawnFigure(Figures figure, Colors color, Vector position) {
        Figure newFigure;
        switch (figure) {
            case Pawn:
                newFigure = new Pawn(color, this, position);
                break;
            case Rook:
                newFigure = new Rook(color, this, position);
                break;
            case Knight:
                newFigure = new Knight(color, this, position);
                break;
            case Queen:
                newFigure = new Queen(color, this, position);
                break;
            case Bishop:
                newFigure = new Bishop(color, this, position);
                break;
            case King:
                newFigure = new King(color, this, position);
                if (color == Colors.White) {
                    whiteKingExists = true;
                    whiteKingPosition = position;
                }
                else {
                    blackKingExists = true;
                    blackKingPosition = position;
                }
                break;
            default:
                newFigure = new Pawn(color, this, position);
        }
        board[position.X][position.Y] = newFigure;
        if (model.getView() != null)
            model.getView().showFigure(position, figure, color);
    }
    /**
     * Create default chess board
     */
    public void spawnDefaultFigures() {
        for (int i = 0; i < size_X; i++) {
            spawnFigure(Figures.Pawn, Colors.White, new Vector(i, 1));
            spawnFigure(Figures.Pawn, Colors.Black, new Vector(i, size_Y - 2));
        }
        spawnFigure(Figures.Rook, Colors.White, new Vector(0, 0));
        spawnFigure(Figures.Rook, Colors.White, new Vector(7, 0));
        spawnFigure(Figures.Rook, Colors.Black, new Vector(0, 7));
        spawnFigure(Figures.Rook, Colors.Black, new Vector(7, 7));

        spawnFigure(Figures.Knight, Colors.White, new Vector(1, 0));
        spawnFigure(Figures.Knight, Colors.White, new Vector(6, 0));
        spawnFigure(Figures.Knight, Colors.Black, new Vector(1, 7));
        spawnFigure(Figures.Knight, Colors.Black, new Vector(6, 7));

        spawnFigure(Figures.Bishop, Colors.White, new Vector(2, 0));
        spawnFigure(Figures.Bishop, Colors.White, new Vector(5, 0));
        spawnFigure(Figures.Bishop, Colors.Black, new Vector(2, 7));
        spawnFigure(Figures.Bishop, Colors.Black, new Vector(5, 7));

        spawnFigure(Figures.Queen, Colors.White, new Vector(3, 0));
        spawnFigure(Figures.Queen, Colors.Black, new Vector(3, 7));

        spawnFigure(Figures.King, Colors.White, new Vector(4, 0));
        spawnFigure(Figures.King, Colors.Black, new Vector(4, 7));
    }
    /**
     * Get a figure from this position
     * @param position position of the figure
     */
    public Figure getFigure(Vector position)
    {
        return board[position.X][position.Y];
    }

    /**
     * print the game board to the console
     */
    public void printBoard() {
        System.out.println("");
        for (int i = board.length - 1; i > -1; i--) {
            for (int j = 0; j < board[0].length; j++) {
                Figure f = getFigure(new Vector(j,i));
                if (f == null)
                    System.out.print("[] ");
                else
                    System.out.print(f.figureColorAndSymbol + " ");
            }
            System.out.println("");
        }
    }
    /**
     * returns the type of the figure
     * @param f Figure object
     */
    public static Figures getFigureType(Figure f) {
        if (f instanceof Pawn)
            return Figures.Pawn;
        else if (f instanceof Bishop)
            return Figures.Bishop;
        else if (f instanceof Rook)
            return Figures.Rook;
        else if (f instanceof Knight)
            return Figures.Knight;
        else if (f instanceof Queen)
            return Figures.Queen;
        else if (f instanceof King)
            return Figures.King;
        else
            return null;
    }
    /**
     * returns figure type on index f from Figures
     */
    public static Figures getFigureType(int f) {
        switch (f)
        {
            case 0:
                return Figures.Pawn;
            case 1:
                return Figures.Rook;
            case 2:
                return Figures.Knight;
            case 3:
                return Figures.Bishop;
            case 4:
                return Figures.Queen;
            case 5:
                return Figures.King;
            default:
                return Figures.Pawn;
        }
    }
    /**
     * get all figures of type and color
     */
    public ArrayList<Figure> getAllFiguresOfType(Figures type, Colors color) {
        ArrayList<Figure> res = new ArrayList<>();
        for (int i = 0; i < size_X; i++) {
            for (int j = 0; j < size_Y; j++) {
                Figure f = getFigure(new Vector(i,j));
                if (f != null && f.getColor() == color && getFigureType(f) == type)
                    res.add(f);
            }
        }
        return res;
    }

    /**
     * @param move from what point to what point
     * @param commit show this move on board and control checks and mates
     * @param logging log this move (false if it's redo or undo)
     */
    public void moveFigure(Move move, boolean commit, boolean logging) {
        if (board[move.to.X][move.to.Y] !=null) {
            move.killed = board[move.to.X][move.to.Y];
        }
        else {
            move.killed = null;
        }
        if (move.promoting)
            spawnFigure(move.promotingTo, board[move.from.X][move.from.Y].getColor(), move.to);
        else
            board[move.to.X][move.to.Y] = board[move.from.X][move.from.Y];

        board[move.from.X][move.from.Y] = null;

        Vector destination = move.to;
        Figure f = getFigure(destination);
            f.setPosition(destination);



        if (commit) {
            if (f instanceof Pawn &&
                    ((f.getColor() == Colors.White && move.from.Y == 1) ||
                            (f.getColor() == Colors.Black && move.from.Y == 6))) {
                ((Pawn)f).firstMove = false;
            }
            if (f instanceof King && (move.getLog().compareTo("O-O") == 0 || move.getLog().compareTo("O-O-O") == 0)) {
                ((King)f).firstMove = false;
                if (move.getLog().compareTo("O-O") == 0) {
                    Move rookMove;
                    if (f.getColor() == Colors.White){
                        rookMove = new Move(new Vector(7, 0), new Vector(5, 0));
                    }
                    else{
                        rookMove = new Move(new Vector(7, 7), new Vector(5, 7));
                    }
                    moveFigure(rookMove, true, false);
                }
                else if (move.getLog().compareTo("O-O-O") == 0){
                    Move rookMove;
                    if (f.getColor() == Colors.White){
                        rookMove = new Move(new Vector(0, 0), new Vector(3, 0));
                    }
                    else{
                        rookMove = new Move(new Vector(0, 7), new Vector(3, 7));
                    }
                    moveFigure(rookMove, true, false);
                }
            }
            commit(move, f.getColor(), logging, "");
        }
    }

    /**
     * undo the move
     * @param commit show this move on board and control checks and mates
     */
    public void undoMoveFigure(Move move, boolean commit) {
        if (move.promoting)
            spawnFigure(Figures.Pawn, board[move.to.X][move.to.Y].getColor(), move.from);
        else
            board[move.from.X][move.from.Y] = board[move.to.X][move.to.Y];
        if (move.killed != null) {
            board[move.to.X][move.to.Y] = move.killed;
        }
        else {
            board[move.to.X][move.to.Y] = null;
        }

        Vector back = move.from;
        Figure f = getFigure(back);
        f.setPosition(back);
        if (commit) {
            if (f instanceof Pawn &&
                    ((f.getColor() == Colors.White && move.from.Y == 1) ||
                    (f.getColor() == Colors.Black && move.from.Y == 6)))
                ((Pawn)f).firstMove = true;
            if (f instanceof King && (move.getLog().compareTo("O-O") == 0 || move.getLog().compareTo("O-O-O") == 0))
            {
                ((King)f).firstMove = true;
                if (move.getLog().compareTo("O-O") == 0) {
                    Move rookMove;
                    if (f.getColor() == Colors.White){
                        rookMove = new Move(new Vector(5, 0), new Vector(7, 0));
                    }
                    else{
                        rookMove = new Move(new Vector(5, 7), new Vector(7, 7));
                    }
                    moveFigure(rookMove, true, false);
                }
                else if (move.getLog().compareTo("O-O-O") == 0){
                    Move rookMove;
                    if (f.getColor() == Colors.White){
                        rookMove = new Move(new Vector(3, 0), new Vector(0, 0));
                    }
                    else{
                        rookMove = new Move(new Vector(3, 7), new Vector(0, 7));
                    }
                    moveFigure(rookMove, true, false);
                }
            }
            commit(new Move(move.to, move.from), f.getColor(), false, "");
            if (move.killed != null && model.getView() != null)
                model.getView().showFigure(move.to, getFigureType(move.killed), move.killed.getColor());
        }
    }
    /**
     * showing the move on the board and control for checks and mates
     * @param customLog add custom log to the move (castling for example)
     */
    public void commit(Move move,Colors color, boolean logging, String customLog) {
        //logging = customGame ? false : logging;

        if (logging) {
            logger.log(move, color, customLog);
        }
        Colors colorOpposite = color == Colors.White ? Colors.Black : Colors.White;
        int check_and_mate_status = checkMateControl(colorOpposite, getFigure(move.to));

        switch (check_and_mate_status) {
            case 2:
                System.out.println("CHECKMATE TO " + (colorOpposite == Colors.White ? "WHITE" : "BLACK"));
                if (logging)
                    logger.checkmateMove(color);
                if (model.getView() != null) {
                    model.getView().checkCell(colorOpposite == Colors.White ? whiteKingPosition : blackKingPosition);
                }
                if (color == Colors.White)
                    model.notifyGameState(GameState.Checkmate_By_White);
                else
                    model.notifyGameState(GameState.Checkmate_By_Black);
                break;
            case 1:
                Model.consoleLogger.info("CHECK TO " + (colorOpposite == Colors.White ? "WHITE" : "BLACK"));
                if (logging)
                    logger.checkMove(color);
                if (model.getView() != null) {
                    model.getView().checkCell(colorOpposite == Colors.White ? whiteKingPosition : blackKingPosition);
                }
                model.notifyGameState(GameState.Check);
                break;
            case 0:
                if (model.getView() != null) {
                    model.getView().uncheckCell();
                }
                model.notifyGameState(GameState.Playing);
                break;
        }
        if (model.getView() != null && customLog.isEmpty()) {
            logger.showLog(model.getView());
            model.getView().showMove(move, getFigureType(getFigure(move.to)), color);
        }
        Model.consoleLogger.info("move is commited");
    }

    /**
     * returns True if there is a figure on this position
     */
    public boolean selectFigure(Vector position) {
        if (getFigure(position) != null) {
            selectedFigure = getFigure(position);
            selectedFigure.computeMoves(selectedFigure.getColor() == Colors.White ? whiteKingExists : blackKingExists);
            return true;
        }
        return false;
    }

    /**
     * returns True if the cell is your color
     */
    public boolean isCellSelectable(Vector Cell, Colors color) {
        if (board[Cell.X][Cell.Y] != null && board[Cell.X][Cell.Y].getColor() == color)
            return true;
        else
            return false;
    }

    /**
     * returns -1 if the cell is empty or out of range. Returns 0 if it's white and 1 if it's black
     */
    public int getCellStatus(Vector position) {
        if (!Vector.inRange(position, -1, 8))
            return -1;
        if (board[position.X][position.Y] == null) {
            return -1;
        }
        else {
            if (board[position.X][position.Y].getColor() == Colors.White) {
                return 0;
            }
            else {
                return 1;
            }
        }
    }
    /**
     * returns model
     */
    public Model getModel()
    {
        return model;
    }
    /**
     * returns selected figure
     */
    public Figure getSelectedFigure()
    {
        return selectedFigure;
    }


    Figure promotedPawn;
    Move promotionMove;

    /**
     * visualize the promote move
     */
    public void showPromote(Figure pawn, Move move) {
        promotedPawn = pawn;
        promotionMove = move;
        if (model.getView() != null && !model.AImoves) {
            model.getView().showPromote(pawn.getColor());
        }
        else {
            if (model.AImoves || move.promotingTo == null)
                updatePawn(Figures.Queen);
            else if (move.promotingTo != null)
            {
                updatePawn(move.promotingTo);
            }
        }
    }
    /**
     * visualize the castling move
     */
    public void showCastling(Move king, Move rook, Colors color) {
        if (model.getView() != null) {
            model.getView().showMove(king, Figures.King, color);
            model.getView().showMove(rook, Figures.Rook, color);
                logger.showLog(model.getView());
        }

    }

    /**
     * pawn promoting
     * @param figureType selected type of the figure to promote
     */
    public void updatePawn(Figures figureType) {
        promotionMove.promotingTo = figureType;
        if (board[promotionMove.to.X][promotionMove.to.Y] !=null)
            promotionMove.killed = board[promotionMove.to.X][promotionMove.to.Y];
        else
            promotionMove.killed = null;
        board[promotionMove.to.X][promotionMove.to.Y] = board[promotionMove.from.X][promotionMove.from.Y];
        board[promotionMove.from.X][promotionMove.from.Y] = null;


        Vector pos = promotionMove.to;
        Colors col = promotedPawn.getColor();
        spawnFigure(figureType, col, pos);
        //if (!customGame)
            logger.logPromote(promotionMove, col, figureType);

        commit(promotionMove, col, false, "");
    }

    /**
     * Controls the check
     * @param color color of the king to control the check for
     * @param move control the check if this move will happen
     */
    public boolean possibleCheck(Move move, Colors color) {
        if (((color == Colors.White) && !whiteKingExists) || ((color == Colors.Black) && !blackKingExists))
            return false;

        if (move != null) {
            moveFigure(move, false, false);
        }

        Vector checking = color == Colors.White ? whiteKingPosition : blackKingPosition;

        //Check every enemy figure if they can get the king
        for (int i = 0; i < size_X; i++) {
            for(int j = 0; j < size_Y; j++) {
                Figure f = getFigure(new Vector(i,j));
                if (f != null && f.getColor() == (color == Colors.White ? Colors.Black : Colors.White)) {
                    f.computeMoves(false);
                    if (Vector.contains(f.getHits(), checking)) {
                        if (move != null)
                            undoMoveFigure(move, false);
                        return true;
                    }
                }
            }
        }
        if (move != null)
            undoMoveFigure(move, false);
        return false;
    }

    private boolean canBeBlocked(Colors color, Figure enemy) {
        for (int i = 0; i < size_X; i++) {
            for(int j = 0; j < size_Y; j++) {
                Figure f = getFigure(new Vector(i,j));
                if (f != null && f.getColor() == color) {
                    f.computeMoves(true);
                    if (Vector.contains(f.getHits(), enemy.getPosition())) {
                        return true;
                    }
                    ArrayList<Vector> moves = enemy.getMoves();
                    for (int k = 0; k < moves.size(); k++) {
                        if (Vector.contains(f.getHits(), moves.get(i))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * contol the checkmate
     * @param color color of the king
     * @param enemy the enemy which could cause check or even checkmate
     */
    public int checkMateControl(Colors color, Figure enemy) {
        if (((color == Colors.White) && !whiteKingExists) || ((color == Colors.Black) && !blackKingExists))
            return 0;
        Figure king = color == Colors.White ? getFigure(whiteKingPosition) : getFigure(blackKingPosition);
        king.computeMoves(true);
        if (possibleCheck(null, color)) {
            if (model.getView() != null)
                model.getView().checkCell(king.getPosition());
            if (king.getMoves().size() == 0 && !canBeBlocked(color, enemy))
                return 2;
            return 1;
        }
        return 0;
    }


}
