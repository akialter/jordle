import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * The code for Jordle app.
 * @author Phat Bui
 * @version 1.0
 */
public class Jordle extends Application {
    private static int col = 0;
    private static int row = 0;
    private static String mystery; // the word to be guessed
    private static final String DEFAULT_MSG = "Try guessing a word!";
    private static Label status = new Label(DEFAULT_MSG); // default status
    private static boolean isOver = false; // is the game over?

    /**
     * Main class to run the app.
     * @param args the args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // get the random word to be guessed
        mystery = getRandomWord();
        // set the stage's title
        primaryStage.setTitle("Jordle");

        // root pane
        VBox root = new VBox();

        // title pane (Jordle)
        StackPane title = new StackPane();
        Text text = new Text("Jordle");
        text.setFont(Font.font("Calibri", 50));
        title.getChildren().add(text);
        title.setMinSize(200, 150);

        // grid pane (5x6) to type the guesses
        HBox mid = new HBox();
        GridPane grid = new GridPane();
        StackPane[][] boxes = new StackPane[5][6]; // an array to hold gridboxes
        Rectangle[][] recs = new Rectangle[5][6]; // rectangles to hold colors
        Text[][] inputs = new Text[5][6]; // an array to hold the letters in the boxes
        // call restart() to instantiate the gridboxes (could be used to clear them too)
        restart(grid, boxes, recs, inputs);
        // style grid pane
        grid.setHgap(5);
        grid.setVgap(5);
        mid.getChildren().addAll(grid);
        mid.setAlignment(Pos.TOP_CENTER);
        mid.setPadding(new Insets(0, 0, 30, 0));

        // bottom pane for options and messages
        HBox bot = new HBox();
        // instruction button
        Button ins = new Button("Instructions");
        ins.setFocusTraversable(false);
        // lambda expression
        ins.setOnAction((event) -> {
            Stage window = new Stage();
            window.setTitle("Instructions");
            VBox pane = new VBox();
            pane.getChildren().add(new Text(String.format("Welcome to Jordle! You will have"
                    + " 6 attempts to guess\nthe mystery word. Type your guess and press enter!\nGreen"
                    + " letters are both correct and in the correct spot.\nYellow letters are correct"
                    + " but in the wrong spot.\nGray letters are incorrect.")));
            window.setScene(new Scene(pane, 300, 200));
            window.setResizable(false);
            window.show();
        });
        // restart button
        Button res = new Button("Restart");
        res.setFocusTraversable(false);
        // anonymous class
        res.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // generate new word
                mystery = getRandomWord();
                // reset the grid
                restart(grid, boxes, recs, inputs);
                // reset the indexes (row & col)
                setRow(0);
                setCol(0);
            }
        });
        // style bot pane
        bot.setAlignment(Pos.BASELINE_CENTER);
        bot.setPadding(new Insets(15, 0, 15, 0));
        bot.setSpacing(10);
        bot.getChildren().addAll(status, ins, res);
        bot.setMinSize(200, 50);

        // add all panes to root pane
        root.getChildren().addAll(title, mid, bot);
        primaryStage.setScene(new Scene(root, 500, 500));
        // primaryStage.setResizable(false);
        primaryStage.show();

        // get user's alphabetic inputs only
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            // only accept aphabetic input
            if (key.getText().matches("[a-zA-Z]+") && !isOver) {
                // do nothing if the gridboxes are full
                if (col == 5 || row == 6) {
                    return;
                }
                inputs[col][row] = new Text(key.getText().toUpperCase());
                boxes[col][row].getChildren().add(inputs[col][row]);
                setCol(col + 1);
            }

            // if user inputs backspace then remove the latest text box
            if (key.getCode() == KeyCode.BACK_SPACE && col != 0 && !isOver) {
                setCol(col - 1);
                boxes[col][row].getChildren().remove(inputs[col][row]);
                inputs[col][row].setText("");
            }

            // if user inputs enter, then get the user's guessed word
            if (key.getCode() == KeyCode.ENTER && !isOver) {
                // gridboxes are full so do nothing
                if (row == 6) {
                    return;
                }
                // store the word in a String
                String guess = "";
                for (int i = 0; i < inputs.length; i++) {
                    guess += inputs[i][row].getText();
                }
                // pop-up error window if the word is incomplete
                if (guess.length() != 5) {
                    Alert errorAlert = new Alert(AlertType.ERROR);
                    errorAlert.setHeaderText("Input not valid");
                    errorAlert.setContentText("Please enter a 5-letter word.");
                    errorAlert.showAndWait();
                } else {
                    String[] result = validateWord(guess); // validate the word
                    int greenCount = 0; // count green letters, 5 = user won
                    for (int i = 0; i < result.length; i++) {
                        switch (result[i]) {
                        case "GREEN":
                            recs[i][row].setFill(Color.GREEN);
                            greenCount++;
                            break;
                        case "YELLOW":
                            recs[i][row].setFill(Color.YELLOW);
                            break;
                        default:
                            recs[i][row].setFill(Color.GRAY);
                        }
                    }
                    // stop the game when the user won
                    if (greenCount == 5) {
                        setStatus("Congratulations! You’ve guessed the word!");
                        setCol(5);
                        setRow(6);
                        setIsOver(true);
                    } else {
                        setCol(0); // reset index for next row
                        setRow(row + 1); // increment row index

                        // check if the game is over (out of attempt)
                        if (row == 6) {
                            setIsOver(true);
                            setStatus("Game over. The word was " + mystery + ".");
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets the index of the column which are used in the 2D StackPane[][],
     * Rectangle[][] and Text[][] array.
     * @param i the column index
     */
    private static void setCol(int i) {

        col = i;
    }

    /**
     * Sets the index of the row which are used in the 2D StackPane[][],
     * Rectangle[][] and Text[][] array.
     * @param i the row index
     */
    private static void setRow(int i) {
        row = i;
    }

    /**
     * Sets the boolean isOver that tells whether the game is over or not.
     * @param bool isOver
     */
    private static void setIsOver(boolean bool) {
        isOver = bool;
    }

    /**
     * Instantiate each Text object in the 2D Text[][] array with an empty String.
     * @param arr the arr
     */
    private static void fillArray(Text[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = new Text("");
            }
        }
    }

    /**
     * This method takes the user's guess and return a String[] array represents the
     * color of each letter in the guessed word. "GREEN" means the letter is correct and
     * in the correct index. "YELLOW" means the letter is correct but in the wrong index.
     * "GRAY" means the letter is incorrect. Return the String[], for example,
     * {"GREEN", "GRAY", "GREEN", "YELLOW", "YELLOW"}.
     * @param word the String represents the user's guess
     * @return String[]
     */
    private static String[] validateWord(String word) {
        // return array represents the user's current guess for each char
        String[] result = new String[5];
        // convert to lower case
        word = word.toLowerCase();
        int[] list = listOfChar(word); // array to hold number of repeats of each char
        // compare each char and add the notations (green, yellow, gray)
        // to the return String[] accordingly
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == mystery.charAt(i)) {
                result[i] = "GREEN";
                if (list[word.charAt(i) - 97] > 0) {
                    list[word.charAt(i) - 97]--;
                }
            } else {
                result[i] = "GRAY";
            }
        }
        for (int i = 0; i < word.length(); i++) {
            if (!result[i].equals("GREEN")) {
                if (mystery.contains(String.valueOf(word.charAt(i)))
                        && list[word.charAt(i) - 97] > 0) {
                    result[i] = "YELLOW";
                    list[word.charAt(i) - 97]--;
                }
            }
        }
        return result;
    }

    /**
     * Gets the random word as the mystery word
     * @return the random word
     */
    private static String getRandomWord() {
        return Words.list.get((int) (Math.random() * Words.list.size()));
    }

    /**
     * The method stores the number of occurrences of each letter in a String in a
     * int[] array and return it. Each index of the array is the ASCII value of the
     * letter and the stored int in each element is the number of occurrences of the
     * letter.
     * @param str the String
     * @return int[] array of occurrences (26 elements represent the alphabet)
     */
    private static int[] countOccur(String str) {
        char[] arr = str.toCharArray();
        int[] result = new int[26];
        for (int i = 0; i < arr.length; i++) {
            int count = 0;
            for (int j = 0; j < arr.length; j++) {
                count = (arr[j] == arr[i]) ? count + 1 : count;
            }
            result[arr[i] - 97] = count;
        }
        return result;
    }

    /**
     * The method takes the user's input as String and compares it with the mystery
     * word. Return int[] array represents the number of occurrences of each letter
     * that both the guess and mystery words share. Each index of the array is the ASCII
     * value of the letter and the stored int in each element is the number of occurrences
     * of the letter.
     * @param str the String represents the user's input
     * @return int[] array of occurrences (26 elements represent the alphabet)
     */
    private static int[] listOfChar(String str) {
        // hash arrays
        int[] guessArr = countOccur(str);
        int[] mystArr = countOccur(mystery);
        // intersect both hash arrays to a new hash array and return
        int[] result = new int[26];
        for (int i = 0; i < result.length; i++) {
            if (mystArr[i] > 0 && guessArr[i] > 0) {
                result[i] = (mystArr[i] < guessArr[i]) ? mystArr[i] : guessArr[i];
            }
        }
        return result;
    }

    /**
     * Restart the game (set everything to default)
     * @param grid   the GridPane
     * @param boxes  the StackPane on each grid
     * @param recs   the Rectangle shape on StackPane
     * @param inputs the Text on StackPane
     */
    private static void restart(GridPane grid, StackPane[][] boxes, Rectangle[][] recs, Text[][] inputs) {
        // restore to default (can also be used to instantiate)
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[0].length; j++) {
                boxes[i][j] = new StackPane();
                recs[i][j] = new Rectangle(30, 30);
                recs[i][j].setFill(Color.WHITE);
                boxes[i][j].getChildren().add(recs[i][j]);
                boxes[i][j].setStyle("-fx-border-color: #000000");
                grid.add(boxes[i][j], i, j);
            }
        }
        fillArray(inputs);
        setIsOver(false);
        setStatus(DEFAULT_MSG);
    }

    /**
     * Sets the current status of the game
     * @param str the String reprents current status
     */
    private static void setStatus(String str) {
        status.setText(str);
    }
}
