package chess.model;

import chess.model.figures.*;
import chess.view.Window;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;



public class Log {
    private Board board;

    public Log(Board board)
    {
        this.board = board;
    }

    private Move[] loading_moves;

    /**
     * list of white moves
     */
    public ArrayList<Move> whiteMoves = new ArrayList<>();
    /**
     * list of black moves
     */
    public ArrayList<Move> blackMoves = new ArrayList<>();

    /**
     * current pair of moves
     */
    public int currentMove = 0;


    private String event = "???";
    private String site = "???";
    private String date = "1970.01.01";
    private String round = "0";
    private String white = "???";
    private String black = "???";
    private String rounds = "";
    private String result = "";

//    [Event "F/S Return Match"]
//            [Site "Belgrade, Serbia JUG"]
//            [Date "1992.11.04"]
//            [Round "29"]
//            [White "Fischer, Robert J."]
//            [Black "Spassky, Boris V."]
//            [Result "1/2-1/2"]


    /**
     * add this move to log
     * @param move move to be added
     * @param color whose move it was?
     * @param text custom log message
     */
    public void log(Move move, Colors color, String text) {
        move.color = color;
        if (text == "")
            moveToString(move);
        else
            move.customLog(text);
        if (((color == Colors.White) && currentMove < whiteMoves.size()) ||
                ((color == Colors.Black && currentMove < blackMoves.size()))) {
            if (color == Colors.White) {
                whiteMoves = new ArrayList(whiteMoves.subList(0, currentMove));
                blackMoves = new ArrayList(blackMoves.subList(0, currentMove));
            }
            else {
                whiteMoves = new ArrayList(whiteMoves.subList(0, currentMove+1));
                blackMoves = new ArrayList(blackMoves.subList(0, currentMove));

            }
            Model.consoleLogger.info("game history was edited");

        }
        if (color == Colors.White) {
            whiteMoves.add(move);
        }
        else {
            blackMoves.add(move);
        }

    }
    /**
     * visualize the log
     */
    public void showLog(Window view) {
        int startIndex = currentMove / 10;
        int lastIndex;
        if (whiteMoves.size() >= 10 && whiteMoves.size() / 10 >= startIndex)
            lastIndex = 10;
        else
            lastIndex = whiteMoves.size() % 10;
        String[][] moves = new String[lastIndex][2];
        for (int i = 0; i < moves.length; i++) {
            if (whiteMoves.size() > i + 10*startIndex)
                moves[i][0] = whiteMoves.get(i + 10*startIndex).getLog();
            else
                moves[i][0] = "";
            if (blackMoves.size() > i + 10*startIndex)
                moves[i][1] = blackMoves.get(i + 10*startIndex).getLog();
            else
                moves[i][1] = "";
        }
        view.showPackInLog(startIndex*10 + 1, moves);
    }
    /**
     * go to next pair of moves
     */
    public void nextMove()
    {
        currentMove++;
    }
    /**
     * add promotion move to log
     * @param move move to be added
     * @param color whose move it was?
     * @param promotingTo figure type to be promoted into
     */
    public void logPromote(Move move, Colors color, Figures promotingTo) {
        log(move, color, "");
        move.removeFigureSymbol();
        if (color == Colors.White)
            whiteMoves.get(currentMove).addToLog("=" + getFigureSymbol(promotingTo));
        else
            blackMoves.get(currentMove).addToLog("=" + getFigureSymbol(promotingTo));
    }

    private void moveToString(Move move) {
        char symbol = getFigureSymbol(board.getFigure(move.to));
        if (symbol != 0)
            move.addToLog(Character.toString(symbol));
        move.addToLog(getAdditionalInformation(move));
        if (move.killed != null)
            move.addToLog("x");
        move.addToLog(getColumnSymbol(move.to.X) + (move.to.Y + 1));
    }
    /**
     * load log
     * @param file file to load from
     */
    public void load(String file) {
        try {
            FileInputStream fis=new FileInputStream(file);
            Scanner sc=new Scanner(fis);
            int index = 0;
            while(sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.isEmpty()) {
                    continue;
                }
                if (s.charAt(0) == '[') {
                    s = s.substring(1, s.length() - 1);
                }
                switch (index) {
                    case 0:
                        event = s;
                        break;
                    case 1:
                        site = s;
                        break;
                    case 2:
                        date = s;
                        break;
                    case 3:
                        round = s;
                        break;
                    case 4:
                        white = s;
                        break;
                    case 5:
                        black = s;
                        break;
                    case 6:
                        int lastIndexOf = s.lastIndexOf(" ");
                        rounds = s.substring(0, lastIndexOf);
                        result = s.substring(lastIndexOf+1);
                        break;
                }
                index++;
            }
            sc.close();

            fromString();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * save log
     * @param file file for saving
     */
    public void save(String file) {
        if (result == "")
            result = "*";
        try {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("[" + event + "]");
            printWriter.println("[" + site + "]");
            printWriter.println("[" + date + "]");
            printWriter.println("[" + whiteMoves.size() + "]");
            printWriter.println("[" + white + "]");
            printWriter.println("[" + black + "]\n");

            String rounds = "";
            for (int i = 0; i < whiteMoves.size(); i++) {
                rounds += (i+1) +"." + whiteMoves.get(i).getLog() + " ";
                if (blackMoves.size() > i)
                    rounds +=blackMoves.get(i).getLog() + " ";
            }
            rounds +=result;
            printWriter.print(rounds);
            printWriter.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * get the log of the move
     * @param moveIndex index of the pair of moves
     * @color color of the figure
     */
    public String getMoveString(int moveIndex, Colors color) {
        if (color == Colors.White && whiteMoves.size() >= moveIndex)
            return whiteMoves.get(moveIndex).getLog();
        else if (color == Colors.Black && blackMoves.size() >= moveIndex)
            return blackMoves.get(moveIndex).getLog();
        else
            return "";
    }
    /**
     * mark the move as "check"
     * @param color whose color was it?
     */
    public void checkMove(Colors color) {
        if (currentMove >= whiteMoves.size())
            return;
        if (color == Colors.White)
        {
            if (!whiteMoves.get(currentMove).check) {
                whiteMoves.get(currentMove).check = true;
                whiteMoves.get(currentMove).addToLog("+");
            }
        }
        else if (!blackMoves.get(currentMove).check) {
            blackMoves.get(currentMove).checkmate = true;
            blackMoves.get(currentMove).addToLog("+");
        }
    }
    /**
     * mark the move as "checkmate"
     * @param color whose color was it?
     */
    public void checkmateMove(Colors color) {
        if (color == Colors.White) {
            if (!whiteMoves.get(currentMove).checkmate) {
                whiteMoves.get(currentMove).checkmate = true;
                whiteMoves.get(currentMove).addToLog("#");
            }
            result = "1/0";
        }
        else {
            if (!blackMoves.get(currentMove).checkmate) {
                blackMoves.get(currentMove).checkmate = true;
                blackMoves.get(currentMove).addToLog("#");
            }
            result = "0/1";
        }
    }
    /**
     * in case you would give up
     * @param color who gives up
     */
    public void surrender(Colors color) {
        if (color == Colors.White) {
            result = "0/1";
        }
        else {
            result = "1/0";
        }
    }
    /**
     * add column or row of the start position on this move, in case it's needed
     * @param move move which needs additional information
     */
    private String getAdditionalInformation(Move move) {
        String res = "";
        boolean row_added = false;
        boolean col_added = false;
        Figure f = board.getFigure(move.to);
        ArrayList<Figure> figures = board.getAllFiguresOfType(Board.getFigureType(f), f.getColor());
        figures.remove(f);
        for (int i = 0; i < figures.size(); i++) {
            if (Vector.contains(figures.get(i).getMoves(), move.to)) {
                if (figures.get(i).getPosition().X == move.from.X)
                    row_added = true;
                if (figures.get(i).getPosition().Y == move.from.Y)
                    col_added = true;

            }
        }
        if (col_added)
            res += getColumnSymbol(move.from.X);
        if (row_added)
            res += move.from.Y + 1;

        return res;
    }

    /**
     * get the figure's symbol
     * @param f Figure to get the symbol
     */
    public static char getFigureSymbol(Figure f) {
        if (f instanceof Bishop)
            return 'B';
        else if (f instanceof Rook)
            return 'R';
        else if (f instanceof Knight)
            return 'N';
        else if (f instanceof Queen)
            return 'Q';
        else if (f instanceof King)
            return 'K';
        else
            return 0;
    }
    /**
     * get the figure's symbol
     * @param f Figure's type    to get the symbol
     */
    public static String getFigureSymbol(Figures f) {
        switch (f) {
            case Rook: return "R";
            case Knight: return "N";
            case Bishop: return "B";
            case Queen: return "Q";
            case King: return "K";
            default:
                return "";
        }
    }
    /**
     * get column symbol from int
     * @param X column index
     */
    public static String getColumnSymbol(int X) {
        String chars = "abcdefgh";
        return ""+chars.charAt(X);
    }
    /**
     * get figure type from symbol
     * @param symbol symbol to get type
     */
    public static Figures getFigureType(char symbol) {
        if (symbol == 'B')
            return Figures.Bishop;
        else if (symbol == 'R')
            return Figures.Rook;
        else if (symbol == 'N')
            return Figures.Knight;
        else if (symbol == 'Q')
            return Figures.Queen;
        else if (symbol == 'K')
            return Figures.King;
        else
            return Figures.Pawn;
    }
    /**
     * get the figure of this color from the X column
     */
    public Figure getFigureOnCol(Figures figureType, int X, Colors color)
    {
        for (int i = 0; i < 8; i++) {
            Figure f = board.getFigure(new Vector(X, i));
            if (getFigureType(getFigureSymbol(f)) == figureType && f.getColor() == color)
            {
                return f;
            }
        }

        return null;
    }
    /**
     * get the figure of this color from the X row
     */
    public Figure getFigureOnRow(Figures figureType, int Y, Colors color)
    {
        for (int i = 0; i < 8; i++) {
            Figure f = board.getFigure(new Vector(i, Y));
            if (getFigureType(getFigureSymbol(f)) == figureType && f.getColor() == color)
            {
                return f;
            }
        }

        return null;
    }

    private void fromString() {
        String[] moves_str = rounds.split(" ");
        Move[] moves = new Move[moves_str.length];
        for (int i = 0; i < moves.length; i++) {
            Move res = new Move(new Vector(0, 0), new Vector(0, 0));
            char end = moves_str[i].charAt(moves_str[i].length() - 1);
            if (moves_str[i].charAt(1) == '.') {
                moves_str[i] = moves_str[i].substring(2);
            }
            if (end == '+') {
                res.check = true;
                moves_str[i] = moves_str[i].substring(0, moves_str[i].length() - 1);
            } else if (end == '#') {
                res.checkmate = true;
                moves_str[i] = moves_str[i].substring(0, moves_str[i].length() - 1);
            }
            String tmp = moves_str[i].substring(moves_str[i].length() - 2);
            if (tmp.charAt(0) == '=') {
                moves_str[i] = moves_str[i].substring(0, moves_str[i].length() - 2);
                res.promoting = true;
                res.promotingTo = Log.getFigureType(tmp.charAt(1));
            }
            if (moves_str[i].compareTo("O-O-O") == 0) {
                if (i % 2 == 0) {
                    res.from = new Vector(4, 0);
                    res.to = new Vector(2, 0);

                } else {
                    res.from = new Vector(4, 7);
                    res.to = new Vector(2, 7);
                }
            } else if (moves_str[i].compareTo("O-O") == 0) {
                if (i % 2 == 0) {
                    res.from = new Vector(4, 0);
                    res.to = new Vector(6, 0);
                } else {
                    res.from = new Vector(4, 7);
                    res.to = new Vector(6, 7);
                }
            } else {

                Figures type = Figures.Pawn;

                char symbol = moves_str[i].charAt(0);
                if (Character.isUpperCase(symbol)) {
                    if (symbol == 'Q')
                        System.out.println("APSODJ");
                    type = getFigureType(symbol);
                    moves_str[i] = moves_str[i].substring(1);
                }
                if (moves_str[i].contains("x"))
                    moves_str[i] = moves_str[i].replace("x", "");
                if (moves_str[i].compareTo("d7") == 0)
                    System.out.println("ASDOpj");
                boolean col_described = false;
                boolean row_described = false;
                int describing_X = 0;
                int describing_Y = 0;
                if (moves_str[i].length() > 2) {

                    char desc = moves_str[i].charAt(0);
                    moves_str[i] = moves_str[i].substring(1);

                    if ("abcdefgh".contains("" + desc)) {
                        col_described = true;
                        describing_X = getCol(desc);
                    } else if ("12345678".contains("" + desc)) {
                        row_described = true;
                        describing_Y = Integer.parseInt("" + desc) - 1;
                    }
                }

                res.to = new Vector(getCol(moves_str[i].charAt(0)), Integer.parseInt("" + moves_str[i].charAt(1)) - 1);

                Figure f = new Figure() {
                };
                if (col_described && row_described)
                    f = board.getFigure(new Vector(describing_X, describing_Y));
                else if (col_described)
                    f = getFigureOnCol(type, describing_X, i % 2 == 0 ? Colors.White : Colors.Black);
                else if (row_described)
                    f = getFigureOnRow(type, describing_Y, i % 2 == 0 ? Colors.White : Colors.Black);
                else
                {
                    ArrayList<Figure> figures = board.getAllFiguresOfType(type, i % 2 == 0 ? Colors.White : Colors.Black);
                    for (int j = 0; j < figures.size(); j++) {
                        figures.get(j).computeMoves(true);
                        if (Vector.contains(figures.get(j).getMoves(), res.to)) {
                            f = figures.get(j);
                            break;
                        }
                    }
                }

                res.from = f.getPosition();
            }
            moves[i] = res;
            if (moves[i].from == null)
                System.out.println("PAOSJD");
            board.getModel().selectCell(moves[i].from);
            board.getModel().selectCell(moves[i].to);

        }

    }

    private int getCol(char symbol)
    {
        switch (symbol)
        {
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            case 'f':
                return 5;
            case 'g':
                return 6;
            case 'h':
                return 7;
            default:
                return -1;
        }

    }
}
