package chess.view;

import chess.controller.Controller;
import chess.model.*;
import chess.model.figures.Figures;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Window{
    private Controller ctrl;

    private JFrame frame;

    private JPanel board;
    private JPanel content;

    private JMenuBar menu;
    private JButton restartBtn;

    private JButton surrenderBtn;
    private JButton undoBtn;
    private JButton redoBtn;

    private JCheckBox whiteFig;
    private JCheckBox whiteMove;

    private JPanel promote;

    private JLabel timer;

    private Cell[][] cells = new Cell[BOARD_HEIGHT][BOARD_WIDTH];
    private JPanel[][] log = new JPanel[10][3];

    private static Dimension WINDOW_SIZE =new Dimension(1040,880);
    private static Dimension MENU_SIZE = new Dimension(1100, 40);
    private static Dimension BOARD_SIZE =new Dimension(800,800);
    private static Dimension PANEL_SIZE = new Dimension(200,800);
    private static Dimension CELL_SIZE = new Dimension(100,100);

    private static int BOARD_HEIGHT=8;
    private static int BOARD_WIDTH=8;

    private static String ICONPATH="pics/";
    private static String PICS[] = { "pawn", "rook", "knight", "bishop", "queen", "king"};
    private static String ROW_LABEL = "ABCDEFGH";

    private static Color BACKGROUND_COLOR = new Color(120, 94, 84);
    private static Color LOG_COLOR = new Color(98, 77, 68);
    private static Color TEXT_COLOR = new Color(237, 232,220);

    private static Font DEFAULT_FONT = new Font("Verdana", Font.BOLD, 15);

    private ArrayList<Vector> last_moves;

    private boolean checked = false;
    private Vector checkedCell = new Vector(-1,-1);

    private boolean white = true;
    private JLabel turn;

    /**
     * create the Window
     * @param ctrl controller must be added
     */
    public Window(Controller ctrl)
    {
        this.ctrl = ctrl;
    }

    /**
     * init the Window
     */
    public void initWindow()
    {
        frame=new JFrame("CHESS!");
        frame.setLayout(new BorderLayout());
        frame.setSize(WINDOW_SIZE);
        frame.setResizable(false);
        // MENU
        menu=new JMenuBar();
        menu.setBackground(Color.WHITE);
        menu.setBorderPainted(false);
        menu.setSize(MENU_SIZE);
        menu.add(menubar_SetUpNewGameButton());

        // ADD LOAD AND SAVE BUTTONS
        JButton load = new JButton();
        setPic(load, "download.png");
        load.setBackground(Color.WHITE);
        load.setBorderPainted(false);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoad();
            } // OPENING THE LOAD DIALOG
        });
        menu.add(load);
        JButton upload = new JButton();
        setPic(upload, "upload.png");
        upload.setBackground(Color.WHITE);
        upload.setBorderPainted(false);
        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSave();
            } // OPENING THE SAVE DIALOG
        });
        menu.add(upload);
        frame.add(menu, BorderLayout.NORTH);

        // CONTENT
        content = new JPanel(new FlowLayout());
        content.setBackground(BACKGROUND_COLOR);
        frame.add(content, SwingConstants.CENTER);

        board = getBoard();
        content.add(board);

        // RIGHT PANEL (LOG + ARROWS + SURRENDER + TIME)
        JPanel rightPanel = new JPanel(new FlowLayout());
        rightPanel.setPreferredSize(PANEL_SIZE);
        rightPanel.setBackground(BACKGROUND_COLOR);
        content.add(rightPanel);

        // LOG PANEL

        rightPanel.add(setUpLog());

        // ARROWS
        JPanel arrows = new JPanel(new BorderLayout());
        arrows.setPreferredSize(new Dimension(200, 50));
        arrows.setBackground(null);
        rightPanel.add(arrows);

        redoBtn = new MyButton("  NEXT  ");
        redoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctrl.redo();
            }
        });
        undoBtn = new MyButton("  PREV  ");
        undoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctrl.undo();
            }
        });
        arrows.add(undoBtn, BorderLayout.WEST);
        arrows.add(redoBtn, BorderLayout.EAST);

        // SURRENDER
        surrenderBtn = new MyButton("SURRENDER");
        surrenderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctrl.surrender();
            }
        });
        surrenderBtn.setPreferredSize(new Dimension(200,50));
        rightPanel.add(surrenderBtn);

        // TIME
        timer = new JLabel("5:00", SwingConstants.CENTER);
        timer.setForeground(TEXT_COLOR);
        timer.setFont(DEFAULT_FONT);
        rightPanel.add(timer);



        //initialize the board
        this.frame.setVisible(true);
    }
    /**
     * changes the icon of the move (White/Black)
     */
    public void turnSide()
    {
        white = !white;
        if (white)
            setPic(turn,"wc.png");
        else
            setPic(turn, "bc.png");
    }

    /**
     * Create the game board
     * @return Game board panel
     */
    private JPanel getBoard()
    {
        JPanel board = new JPanel(new GridLayout(BOARD_HEIGHT + 2, BOARD_WIDTH + 2));
        board.setBackground(BACKGROUND_COLOR);
        for (int i = 0; i < BOARD_HEIGHT + 2; i++)
        {
            for (int j = 0; j < BOARD_WIDTH + 2; j++)
            {
                JPanel text = new JPanel(new BorderLayout());
                text.setBackground(BACKGROUND_COLOR);

                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setFont(DEFAULT_FONT);
                label.setForeground(TEXT_COLOR);
                if (j == 0 || j == BOARD_WIDTH + 1)
                {
                    if (i == 0)
                    {
                        if (j == 0) {
                            turn = new JLabel("", SwingConstants.CENTER);
                            setPic(turn, "wc.png");
                            text.add(turn, SwingConstants.CENTER);
                        }
                        else
                            text.add(label);
                    } else if (i == BOARD_HEIGHT + 1){

                        text.add(label);
                    }
                    else
                    {
                        label.setText("" + (BOARD_HEIGHT - i + 1));

                        text.add(label);

                    }
                    board.add(text);
                } else if (i == 0 || i == BOARD_HEIGHT + 1) {
                    label.setText("" + ROW_LABEL.charAt(j - 1));
                    text.add(label);
                    board.add(text);
                } else {
                    Cell tile = new Cell(j - 1, BOARD_HEIGHT - i);
                    //add to list
                    this.cells[j - 1][BOARD_HEIGHT - i] = tile;
                    //add to boardPanel
                    board.add(tile);
                }
            }
        }

        board.setPreferredSize(BOARD_SIZE);

        return board;
    }
    /**
     * clears all colors on the board
     */
    public void setDefaultColors()
    {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                cells[i][j].setDefaultColor();
            }
        }
        if (checked)
            cells[checkedCell.X][checkedCell.Y].setCheckColor();
    }
    /**
     * removes all pictures
     */
    public void setNoPictures()
    {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                cells[i][j].setPic(null, null);
            }
        }
    }
    /**
     * clear the board
     */
    public void resetBoard()
    {
        checked = false;
        setDefaultColors();
        setNoPictures();
    }
    private boolean promoting = false;

    private String dialog(boolean save)
    {
        JFileChooser files = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "PGN files", "pgn");
        files.setFileFilter(filter);
        int returnVal;
        if (save)
            returnVal = files.showSaveDialog(frame);
        else
            returnVal = files.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return files.getSelectedFile().getAbsolutePath();
        }
        else
            return "";
    }
    /**
     * show loading dialog
     */
    public void showLoad()
    {
        String file = dialog(false);
        ctrl.load(file);
    }
    /**
     * show saving dialog
     */
    public void showSave()
    {
        String file = dialog(true);
        ctrl.save(file);
    }
    /**
     * show promote pawn menu
     */
    public void showPromote(Colors color)
    {
        promoting = true;

        // PROMOTE
        promote = new JPanel(new GridLayout(1, 4));
        JDialog jd = new JDialog(frame, Dialog.ModalityType.APPLICATION_MODAL);

        MyButton rook = new MyButton("");
        rook.setPreferredSize(CELL_SIZE);
        rook.setPic("rook", color);
        rook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (promoting)
                {
                    promoting = false;
                    ctrl.promoteSelected(0);
                }
                jd.dispose();
            }
        });
        MyButton knight = new MyButton("");
        knight.setPreferredSize(CELL_SIZE);
        knight.setPic("knight", color);
        knight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (promoting)
                {
                    promoting = false;
                    ctrl.promoteSelected(1);
                }
                jd.dispose();
            }
        });
        MyButton bishop = new MyButton("");
        bishop.setPreferredSize(CELL_SIZE);
        bishop.setPic("bishop", color);
        bishop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (promoting)
                {
                    promoting = false;
                    ctrl.promoteSelected(2);
                }
                jd.dispose();
            }
        });
        MyButton queen = new MyButton("");
        queen.setPreferredSize(CELL_SIZE);
        queen.setPic("queen", color);
        queen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (promoting)
                {
                    promoting = false;
                    ctrl.promoteSelected(3);
                }
                jd.dispose();
            }
        });
        promote.add(rook);
        promote.add(bishop);
        promote.add(knight);
        promote.add(queen);

        jd.addWindowListener(new WindowAdapter() {
            public void windowDeactivated(WindowEvent e) {
                if (promoting)
                {
                    promoting = false;
                    ctrl.promoteSelected(3);
                }
                jd.dispose();
            }
        });
        jd.setAlwaysOnTop(true);
        jd.add(promote);
        jd.setSize(new Dimension(400,100));
        jd.setLocationRelativeTo(frame);
        jd.setResizable(false);
        jd.setVisible(true);

    }
    private void setButtonForCustomGame(JPanel panel, int type, String picPath)
    {
        MyButton btn = new MyButton("");
        btn.setPreferredSize(CELL_SIZE);
        btn.setPic(picPath, Colors.White);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (whiteFig.isSelected())
                    ctrl.spawn(type, Colors.White);
                else
                    ctrl.spawn(type, Colors.Black);
            }
        });
        panel.add(btn);
    }
    /**
     * show custom figures select
     */
    public void showCustom()
    {
        ctrl.customGame();

        // PROMOTE
        JPanel selector = new JPanel(new GridLayout(1, 9));
        JDialog jd = new JDialog(frame);

        setButtonForCustomGame(selector, 0, "pawn");
        setButtonForCustomGame(selector, 1, "rook");
        setButtonForCustomGame(selector, 2, "knight");
        setButtonForCustomGame(selector, 3, "bishop");
        setButtonForCustomGame(selector, 4, "queen");
        setButtonForCustomGame(selector, 5, "king");

        whiteFig = new JCheckBox("White figure");
        whiteMove = new JCheckBox("White starts");

        JButton start = new JButton("START");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctrl.customGameStart(whiteMove.isSelected());
            }
        });

        selector.add(whiteFig);
        selector.add(whiteMove);
        selector.add(start);
        jd.setAlwaysOnTop(true);
        jd.add(selector);
        jd.setSize(new Dimension(800,100));
        jd.setLocationRelativeTo(frame);
        jd.setResizable(false);
        jd.setVisible(true);

    }
    /**
     * show your (or probably not your) win!!!
     */
    public void showWin(GameState state)
    {
        JDialog jd = new JDialog(frame);

        JLabel info = new JLabel((state == GameState.Checkmate_By_White ? "WHITE" : "BLACK") +
                " WON", SwingConstants.CENTER);


        jd.add(info);

        jd.setAlwaysOnTop(true);
        jd.setSize(new Dimension(400,100));
        jd.setLocationRelativeTo(frame);
        jd.setResizable(false);
        jd.setVisible(true);

    }

    private JPanel setUpLog()
    {
        JPanel log = new JPanel();
        log.setBackground(LOG_COLOR);
        log.setLayout(new GridLayout(10, 3));
        log.setPreferredSize(new Dimension(200,640));
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                JPanel logItem = new JPanel(new BorderLayout());
                logItem.setBackground(LOG_COLOR);
                JLabel label;
                    label = new JLabel("", SwingConstants.CENTER);
                    label.setForeground(TEXT_COLOR);
                    label.setFont(DEFAULT_FONT);
                    logItem.add(label);
                this.log[i][j] = logItem;
                log.add(logItem);
            }
        }
        ((JLabel)this.log[0][0].getComponent(0)).setText("1.");
        showCurrentMoveInLog(0, Colors.White);
        return log;
    }

    private int currentInLog = 0;
    private Colors currentColorInLog = Colors.White;
    /**
     * show the current move
     */
    public void showCurrentMoveInLog(int current, Colors color)
    {
        log[currentInLog][currentColorInLog.ordinal() + 1].setBackground(LOG_COLOR);
        currentInLog = current % 10;
        currentColorInLog = color;
        log[currentInLog][currentColorInLog.ordinal() + 1].setBackground(Color.GRAY);


    }
    /**
     * show the list of max to 10 moves
     */
    public void showPackInLog(int startIndex, String[][] list)
    {
        for (int i = 0; i < 10; i++) {
            ((JLabel)this.log[i][0].getComponent(0)).setText("");
            ((JLabel)this.log[i][1].getComponent(0)).setText("");
            ((JLabel)this.log[i][2].getComponent(0)).setText("");
        }
        for (int i = 0; i < list.length; i++) {
            ((JLabel)this.log[i][0].getComponent(0)).setText((startIndex + i) + ".");
            ((JLabel)this.log[i][1].getComponent(0)).setText(list[i][0]);
            ((JLabel)this.log[i][2].getComponent(0)).setText(list[i][1]);
        }
    }
    /**
     * show the time of the move
     */
    public void showTime(Long minutes, Long seconds)
    {
        timer.setText(minutes + ":" + seconds);
    }
    /**
     * set picture to the element
     */
    public void setPic(JLabel panel, String picPath)
    {
        panel.removeAll();
        try{
            if (picPath != null)
            {
                BufferedImage buffer = ImageIO.read(new File(ICONPATH+picPath));
                ImageIcon pic =new ImageIcon(buffer);
                panel.setIcon(pic);
            }
            else
                panel.setIcon(null);
        }
        catch(IOException exception){
            // add ...
            exception.printStackTrace();
        }
    }
    /**
     * set picture to the element
     */
    public void setPic(JButton panel, String picPath)
    {
        panel.removeAll();
        try{
            if (picPath != null)
            {
                BufferedImage buffer = ImageIO.read(new File(ICONPATH+picPath));
                ImageIcon pic =new ImageIcon(buffer);
                panel.setIcon(pic);
            }
            else
                panel.setIcon(null);
        }
        catch(IOException exception){
            exception.printStackTrace();
        }
    }
    /**
     * show possible moves on the board
     */
    public void movesToSelectedCells(ArrayList<Vector> moves)
    {
        last_moves = moves;
        for (int i = 0; i < moves.size(); i++) {
            cells[moves.get(i).X][moves.get(i).Y].setSelectedColor();
        }
    }
    /**
     * clear the desk from possible moves
     */
    public void deselect()
    {
        for (int i = 0; i < last_moves.size(); i++) {
            cells[last_moves.get(i).X][last_moves.get(i).Y].setDefaultColor();
        }
    }
    /**
     * make the cell red (it's check!)
     */
    public void checkCell(Vector check)
    {
        cells[check.X][check.Y].setCheckColor();
        checked = true;
        checkedCell = check;
    }
    /**
     * it's not check again!
     */
    public void uncheckCell()
    {
        if (checked)
            cells[checkedCell.X][checkedCell.Y].setDefaultColor();
        checked = false;
    }
    /**
     * show the figure moves on the board
     */
    public void showMove(Move move, Figures figure, Colors color) {
        cells[move.from.X][move.from.Y].setPic(null, null);
        cells[move.to.X][move.to.Y].setPic(PICS[figure.ordinal()], color);

    }
    /**
     * show the figure on the board
     */
    public void showFigure(Vector pos, Figures figure, Colors color) {
        cells[pos.X][pos.Y].setPic(PICS[figure.ordinal()], color);
    }

    /**
     * One cell of the board
     */
    public class Cell extends MyButton {
        private static Color COLOR_FIRST = new Color(237, 232,220);
        private static Color COLOR_SECOND = new Color(223,201,182);

        private static Color COLOR_FIRST_SELECTED = new Color(140, 180, 230);
        private static Color COLOR_SECOND_SELECTED = new Color(135, 165, 200);

        private static Color COLOR_CHECK = new Color(230, 90, 90);

        private Color colorSelect = Color.CYAN;
        private Color colorCheck = Color.RED;

        private Vector pos;

        /**
         * Init the cell
         * @param x X-coordinate on the board
         * @param y Y-coordinate on the board
         */
        Cell(int x, int y) {
            super("");
            pos = new Vector(x, y);

            setDefaultColor();

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ctrl.selectCell(pos);
                }
            });
        }
        /**
         * return its color
         */
        public void setDefaultColor()
        {
            if ((pos.X + pos.Y) % 2 == 1)
            {
                setBackground(COLOR_FIRST);
            }
            else
            {
                setBackground(COLOR_SECOND);
            }
        }
        /**
         * make the cell selected (blue)
         */
        public void setSelectedColor()
        {
            if ((pos.X + pos.Y) % 2 == 1)
            {
                setBackground(COLOR_FIRST_SELECTED);
            }
            else
            {
                setBackground(COLOR_SECOND_SELECTED);
            }
        }
        /**
         * make the cell red (check or checkmate)
         */
        public void setCheckColor()
        {
            setBackground(COLOR_CHECK);
        }


    }
    /**
     * custom button with some additions
     */
    public class MyButton extends JButton {

        public MyButton(String text)
        {
            super(text);
            setBackground(LOG_COLOR);
            setForeground(TEXT_COLOR);
            setFont(DEFAULT_FONT);
            setBorderPainted(false);
            setFocusable(false);

        }
        /**
         * set picture to the button
         */
        public void setPic(String picPath, Colors color)
        {
            this.removeAll();
            try{
                if (picPath != null)
                {
                    picPath += color == Colors.White ? "W" : "B";
                    picPath += ".png";
                    BufferedImage buffer = ImageIO.read(new File(ICONPATH+picPath));
                    ImageIcon pic =new ImageIcon(buffer);
                    setIcon(pic);
                }
                else
                    setIcon(null);
            }
            catch(IOException exception){
                exception.printStackTrace();
            }
        }
    }
    /**
     * init the main menu
     */
    public JMenuItem menubar_SetUpNewGameButton()
    {
        JMenuItem new_btn = new JMenu();
        new_btn.setText("New game");

        JMenuItem pva1 = new JMenuItem("Player (White) vs AI");
        JMenuItem pva2 = new JMenuItem("Player (Black) vs AI");
        JMenuItem pvp = new JMenuItem("Player vs Player");
        JMenuItem custom = new JMenuItem("Custom setup");
        pva1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
                ctrl.restart(false, true);
            }
        });
        pva2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
                ctrl.restart(false, false);
            }
        });
        pvp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
                ctrl.restart(true, false);
            }
        });
        custom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctrl.customGame();
                showCustom();
            }
        });
        new_btn.add(pva1);
        new_btn.add(pva2);
        new_btn.add(pvp);
        new_btn.add(custom);
        return new_btn;
    }
}
