package hitori;

import java.io.File;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//Where the game is loaded
public class Hitori extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Puzzle puzzle;
    private BorderPane puzzlePaneEnvelope;
    private PuzzleLabel[][] puzzleLabels;

    /**
     * Default puzzle shown
     * Load and reset button created
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        //Default puzzle
        puzzle = new Puzzle();

        puzzlePaneEnvelope = new BorderPane();

        buildPuzzlePane();

        HBox hbButtons = new HBox();
        hbButtons.setPadding(new Insets(20));
        hbButtons.setAlignment(Pos.CENTER);

        //Load button that enables user to choose a file from their system and load the puzzle
        Button btnLoad = new Button("Load");
        btnLoad.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();

            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null) {
                try {
                    puzzle = new Puzzle(file);
                    buildPuzzlePane();
                } catch (Exception ignored) {

                }
            }
        });

        //Reset button that enables user to reset the puzzle (clear all blacked out cells)
        Button btnReset = new Button("Reset");
        btnReset.setOnAction(e -> {

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to reset?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                puzzle.reset();
                for (int i = 0; i < puzzleLabels.length; i++) {
                    for (int j = 0; j < puzzleLabels[i].length; j++) {
                        puzzleLabels[i][j].refresh();
                    }
                }
            }

        });

        hbButtons.getChildren().addAll(btnLoad, btnReset);

        Scene scene = new Scene(new BorderPane(puzzlePaneEnvelope, null, null, hbButtons, null));

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    /**
     * GridPane to place all cells in
     * Size of grid is according to the size of the puzzle
     */
    private void buildPuzzlePane() {
        GridPane puzzlePane = new GridPane();
        puzzleLabels = new PuzzleLabel[puzzle.getSize()][puzzle.getSize()];

        puzzlePane.setGridLinesVisible(true);

        //Set label (value) per cell/position on grid
        for (int i = 0; i < puzzleLabels.length; i++) {
            for (int j = 0; j < puzzleLabels[i].length; j++) {
                puzzleLabels[i][j] = new PuzzleLabel(i, j);
                GridPane.setConstraints(puzzleLabels[i][j], j, i);
                GridPane.setHalignment(puzzleLabels[i][j], HPos.CENTER);
                puzzlePane.add(puzzleLabels[i][j], j, i, 1, 1);
            }
        }

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        columnConstraints.setFillWidth(true);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setFillHeight(true);

        for (int i = 0; i < puzzleLabels.length; i++) {
            puzzlePane.getColumnConstraints().add(columnConstraints);
            puzzlePane.getRowConstraints().add(rowConstraints);
        }

        puzzlePaneEnvelope.getChildren().clear();

        puzzlePaneEnvelope.setCenter(puzzlePane);

    }




    //This class is in charge of the labelling aspect of the gam i.e the value stored in each cell
    //Also handles all the events (right-click and left-click)
    private class PuzzleLabel extends BorderPane {

        private int row, col;
        private Label label;

        /**
         * Constructor that handles all the events (mouse-clicks)
         * @param row
         * @param col
         */
        public PuzzleLabel(int row, int col) {
            this.row = row;
            this.col = col;
            label = new Label();
            label.setText(puzzle.get(row, col).getValue() + "");
            label.setFont(new Font("Times New Roman", 20));

            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    //Right-click causes cell to be eliminated (covered/blacked out)
                    if (event.getButton() == MouseButton.PRIMARY) {

                        int result = puzzle.eliminate(row, col);

                        //The int returned by the the method eliminate determines the alert message shown
                        if (result == 2 || result == 3) {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setContentText("Error: Constraint " + result + " violated");
                            alert.show();
                        } else {
                            refresh();

                            if (puzzle.isGameOver()) {
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setContentText("You won!");
                                alert.show();
                            }
                        }

                        //Left-click causes initially covered cell to be brought back (back to white)
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        puzzle.reactivate(row, col);
                        refresh();
                    }
                }
            });

            setCenter(label);

            setBorder(new Border(
                    new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

            refresh();

            setPrefSize(30, 30);
        }


        /**
         * When method called, depending on the state (whether supposed to be covered or not)
         * The colour of the cell is determined
         */
        private void refresh() {
            if (puzzle.get(row, col).isCovered()) {
                label.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                label.setBlendMode(BlendMode.DARKEN); //In order to hide the initial value
            } else {
                setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                label.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

            }
        }

    }

}

