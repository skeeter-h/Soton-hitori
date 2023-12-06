package hitori;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

//Class in charge of setting up the puzzle, and monitoring all that happens on the puzzle including resetting.
//Includes methods to check for the breach of any constraints and whether the game has been finished
public class Puzzle {

    private int size;
    private Cell[][] cells;

    public Puzzle() {
        loadDefaultPuzzle();
    }


    /**
     * Second constructor called when puzzle is loaded from the file
     * @param file
     * @throws Exception
     */
    public Puzzle(File file) throws Exception {
        Scanner scanner = new Scanner(file);

        int row = 0;

        //Reads every integer on that line and places it into each cell on that row
        while(scanner.hasNext()) {
            String[] line = scanner.nextLine().split(" ");

            if(row == 0) {
                size = line.length;
                cells = new Cell[size][size];
            }

            for (int i = 0; i < line.length; i++) {
                cells[row][i] = new Cell(Integer.parseInt(line[i]));
            }

            row++;
        }

        scanner.close();
    }

    /**
     * Starting puzzle set up
     */
    private void loadDefaultPuzzle() {
        int[][] values = {{4,8,1,6,3,2,5,7},
                {3,6,7,2,1,6,5,4},
                {2,3,4,8,2,8,6,1},
                {4,1,6,5,7,7,3,5},
                {7,2,3,1,8,5,1,2},
                {3,5,6,7,3,1,8,4},
                {6,4,2,3,5,4,7,8},
                {8,7,1,4,2,3,5,6}};


        size = 8;
        cells = new Cell[size][size];

        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                cells[i][j] = new Cell(values[i][j]);
            }
        }
    }

    /**
     * @param i index row
     * @param j index column
     * @return the cell at specified position
     */
    public Cell get(int i, int j) {
        return cells[i][j];
    }

    /**
     * Returns the size of the grid
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * When called, the puzzle is reset (i.e all the cells are uncovered)
     */
    public void reset() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].uncover();
            }
        }
    }


    /**
     * When method is called, the appropriate cell can be covered (turned black)
     * However, if this breaches constraint 2 or 3, then the cell is uncovered
     * @param i
     * @param j
     * @return
     */
    public int eliminate(int i, int j) {
        cells[i][j].cover();

        if(!checkRule2(i, j)) {
            cells[i][j].uncover();
            System.out.println("Illegal move, constraint 2 violated");
            return 2;
        }

        if(!checkRule3()) {
            cells[i][j].uncover();
            System.out.println("Illegal move, constraint 3 violated");
            return 3;
        }

        return 0;
    }

    /**
     * When called, the initially covered (black) cell is reverted back to white
     * @param i
     * @param j
     */
    public void reactivate(int i, int j) {
        cells[i][j].uncover();
    }

    /**
     * Constraint 2 check (No black cells are right next to each other)
     * @param i index row
     * @param j index column
     * Once the position is input, the cells around are checked
     * @return true if constraint is not violated
     */
    private boolean checkRule2(int i, int j) {
        if(isValidPosition(i - 1, j) && cells[i - 1][j].isCovered())
            return false;

        if(isValidPosition(i + 1, j) && cells[i + 1][j].isCovered())
            return false;

        if(isValidPosition(i, j - 1) && cells[i][j - 1].isCovered())
            return false;

        if(isValidPosition(i, j + 1) && cells[i][j + 1].isCovered())
            return false;

        return true;
    }


    /**
     * Constraint 3 check (all white cells are linked in some way)
     * @return true if constraint is not violated
     */
    private boolean checkRule3() {
        boolean[][] visited = new boolean [size][size];

        boolean found = false;

        //Is every white cell connected?
        //When recurring method (dfs) is terminated, loop breaks on that row and restarts on the next
        for (int i = 0; i < cells.length && !found; i++) {
            for (int j = 0; j < cells.length; j++) {
                if(!cells[i][j].isCovered()) { //Check to make sure cell is not already covered
                    dfs(visited, i, j);
                    found = true;
                    break;
                }
            }
        }

        //Once loop above is finished, check to make sure that all cells were checked
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited.length; j++) {
                if(!cells[i][j].isCovered() && !visited[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**Depth First Search
     * Used for constraint 3
     * Checks if position is valid, unvisited and uncovered. If so, method on the next cells
     * @param visited means that the cell being reached has already been visited once
     * @param i index row
     * @param j index column
     */
    private void dfs(boolean[][] visited, int i, int j) {
        if(!isValidPosition(i, j))
            return; //If position is invalid, the method is terminated

        if(visited[i][j] || cells[i][j].isCovered())
            return; //If the cell had already been visited or covered, the method is terminated

        visited[i][j] = true; //Finished visiting the unvisited nor covered cell

        //If neither statements above are true, the method recurs into the cells next to just visited cell
        dfs(visited, i - 1, j);
        dfs(visited, i + 1, j);
        dfs(visited, i, j - 1);
        dfs(visited, i, j + 1);

    }


    /**
     * Checks if it is a valid position (e.g in case position specified is outside of puzzle
     * @param i index row
     * @param j index column
     * @return true if within puzzle grid
     */
    public boolean isValidPosition(int i, int j) {
        return i >= 0 && i < size && j >= 0 && j < size;
    }


    /**
     *Checks for duplicate values in a row or column
     * @return true if no duplicity, method then used to determine if game is over
     */
    public boolean isGameOver() {

        HashSet<Integer> set = new HashSet<>();

        //looking at each row to check for duplicate values
        for (Cell[] cell : cells) {

            //For every row, the set is cleared so there are no values stored from previous row
            set.clear();

            //Every cell on that row is checked to see whether it is covered, if not covered, the value contained in...
            //in that cell is stored in the hashset
            for (int j = 0; j < cells.length; j++) {

                if (cell[j].isCovered())
                    continue;

                int val = cell[j].getValue();

                if (set.contains(val)) //If the hashset already contains that value, the method will return false
                    return false;

                set.add(val);
            }
        }

        //Checks each column for duplicate values
        for (int j = 0; j < cells.length; j++) {

            set.clear();

            for (Cell[] cell : cells) {

                if (cell[j].isCovered())
                    continue;

                int val = cell[j].getValue();
                if (set.contains(val))
                    return false;

                set.add(val);
            }
        }
        return true;
    }

    public Cell getCells(int i,int j) {
        return cells[i][j];
    }


}
