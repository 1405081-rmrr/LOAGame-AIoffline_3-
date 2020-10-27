package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
public class Checkers extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //---------------------------------------------------------------------

    CheckersBoard board;


    private Button newGameButton;

    private Button resignButton;


    private Label message;

    public void start(Stage stage) {


        message = new Label("Click \"New Game\" to begin.");
        message.setTextFill(Color.rgb(100, 255, 100)); // Light green.
        message.setFont(Font.font(null, FontWeight.BOLD, 18));


        newGameButton = new Button("New Game");
        resignButton = new Button("Resign");

        board = new CheckersBoard();
        board.drawBoard();


        resignButton.setOnAction(e -> board.doResign());
        board.setOnMousePressed(e -> board.mousePressed(e));

        board.relocate(20, 20);
        newGameButton.relocate(370, 120);
        resignButton.relocate(370, 200);
        message.relocate(20, 370);

        resignButton.setManaged(false);
        resignButton.resize(100, 30);
        newGameButton.setManaged(false);
        newGameButton.resize(100, 30);


        Pane root = new Pane();

        root.setPrefWidth(500);
        root.setPrefHeight(480);


        root.getChildren().addAll(board, newGameButton, resignButton, message);
        root.setStyle("-fx-background-color: darkgreen; "
                + "-fx-border-color: darkred; -fx-border-width:3");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Checkers!");
        stage.show();

    }


    // --------------------  Nested Classes -------------------------------


    private static class CheckersMove {
        int fromRow, fromCol;
        int toRow, toCol;

        CheckersMove(int r1, int c1, int r2, int c2) {

            fromRow = r1;
            fromCol = c1;
            toRow = r2;
            toCol = c2;
        }

    }

    private class CheckersBoard extends Canvas {

        CheckersData board;


        boolean gameInProgress;


        int currentPlayer;


        int selectedRow, selectedCol;


        CheckersMove[] legalMoves;
        CheckersBoard() {
            super(324, 324);  // canvas is 324-by-324 pixels
            board = new CheckersData();

        }

        void doResign() {
            if (gameInProgress == false) {
                message.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == CheckersData.RED)
                gameOver("RED resigns.  BLACK wins.");
            else
                gameOver("BLACK resigns.  RED wins.");
        }

        void gameOver(String str) {
            message.setText(str);
            newGameButton.setDisable(false);
            resignButton.setDisable(true);
            gameInProgress = false;
        }

        void doClickSquare(int row, int col) {


            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersData.RED)
                        message.setText("RED:  Make your move.");
                    else
                        message.setText("BLACK:  Make your move.");
                    drawBoard();
                    return;
                }
            }

            if (selectedRow < 0) {
                message.setText("Click the piece you want to move.");
                return;
            }


            for (int i = 0; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                        && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
                    doMakeMove(legalMoves[i]);
                    return;
                }


            message.setText("Click the square you want to move to.");

        }  // end doClickSquare()

        void doMakeMove(CheckersMove move) {

            board.makeMove(move);


            selectedRow = -1;


            if (legalMoves != null) {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++)
                    if (legalMoves[i].fromRow != legalMoves[0].fromRow
                            || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                        sameStartSquare = false;
                        break;
                    }
                if (sameStartSquare) {
                    selectedRow = legalMoves[0].fromRow;
                    selectedCol = legalMoves[0].fromCol;
                }
            }


            drawBoard();

        }

        public void drawBoard() {

            GraphicsContext g = getGraphicsContext2D();
            g.setFont(Font.font(18));


            g.setStroke(Color.DARKRED);
            g.setLineWidth(2);
            g.strokeRect(1, 1, 322, 322);


            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2)
                        g.setFill(Color.LIGHTGRAY);
                    else
                        g.setFill(Color.GRAY);
                    g.fillRect(2 + col * 40, 2 + row * 40, 40, 40);

                    switch (board.pieceAt(row, col)) {
                        case CheckersData.RED:
                            g.setFill(Color.RED);
                            g.fillOval(8 + col * 40, 8 + row * 40, 28, 28);
                            break;
                        case CheckersData.BLACK:
                            g.setFill(Color.BLACK);
                            g.fillOval(8 + col * 40, 8 + row * 40, 28, 28);
                            break;
                        case CheckersData.RED_KING:
                            g.setFill(Color.RED);
                            g.fillOval(8 + col * 40, 8 + row * 40, 28, 28);
                            g.setFill(Color.WHITE);
                            g.fillText("K", 15 + col * 40, 29 + row * 40);
                            break;
                        case CheckersData.BLACK_KING:
                            g.setFill(Color.BLACK);
                            g.fillOval(8 + col * 40, 8 + row * 40, 28, 28);
                            g.setFill(Color.WHITE);
                            g.fillText("K", 15 + col * 40, 29 + row * 40);
                            break;
                    }
                }
            }


            if (gameInProgress) {

                g.setStroke(Color.CYAN);
                g.setLineWidth(4);
                for (int i = 0; i < legalMoves.length; i++) {
                    g.strokeRect(4 + legalMoves[i].fromCol * 40, 4 + legalMoves[i].fromRow * 40, 36, 36);
                }
                if (selectedRow >= 0) {
                    g.setStroke(Color.YELLOW);
                    g.setLineWidth(4);
                    g.strokeRect(4 + selectedCol * 40, 4 + selectedRow * 40, 36, 36);
                    g.setStroke(Color.LIME);
                    g.setLineWidth(4);
                    for (int i = 0; i < legalMoves.length; i++) {
                        if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow) {
                            g.strokeRect(4 + legalMoves[i].toCol * 40, 4 + legalMoves[i].toRow * 40, 36, 36);
                        }
                    }
                }
            }

        }  // end drawBoard()

        public void mousePressed(MouseEvent evt) {
            if (gameInProgress == false)
                message.setText("Click \"New Game\" to start a new game.");
            else {
                int col = (int) ((evt.getX() - 2) / 40);
                int row = (int) ((evt.getY() - 2) / 40);
                if (col >= 0 && col < 8 && row >= 0 && row < 8)
                    doClickSquare(row, col);
            }
        }

    }


    private static class CheckersData {

        static final int
                EMPTY = 0,
                RED = 1,
                RED_KING = 2,
                BLACK = 3,
                BLACK_KING = 4;

        int[][] board;

        CheckersData() {
            board = new int[8][8];
            setUpGame();
        }

        void setUpGame() {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if ((row == 0 && col == 0) || (row == 0 && col == 7) || (row == 7 && col == 0) || (row == 7 && col == 7))
                        continue;
                    if ((row == 0 || row == 7) && (col != 0 || col != 7)) {
                        board[row][col] = RED;
                    } else if ((col == 0 || col == 7) && (row != 0 || row != 7)) {
                        board[row][col] = BLACK;
                    } else {
                        board[row][col] = EMPTY;
                    }
                }
            }
        }  // end setUpGame()

        int pieceAt(int row, int col) {
            return board[row][col];
        }

        void makeMove(CheckersMove move) {
            makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }

        void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = EMPTY;

        }
    }
}
