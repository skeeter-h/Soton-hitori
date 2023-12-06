package hitori;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Random;

class HitoriTest {


    /**
     * Junit test for constraint two, eliminating cells next to chosen cell to make sure it doesn't work, returns false
     * If the actual result of the code returns true then test fails
     */
    @Test
    void testConstraint2() {
        Puzzle puzzle = new Puzzle();

        //Random number is chosen to choose the location of the cell chosen
        Random random = new Random();
        int i = random.nextInt(puzzle.getSize() - 1);
        int j = random.nextInt(puzzle.getSize() - 1);

        //Making sure position is valid and then eliminating (covering)
        if (puzzle.isValidPosition(i, j))
            puzzle.eliminate(i, j);

        Cell cell = puzzle.getCells(i, j);

        //Testing cells next to chosen cell to check whether they can be covered
        if (cell.isCovered()) { //First test for Constraint 2
            //Black out cells next to each other on that row
            if (puzzle.isValidPosition(i - 1, j)) {
                puzzle.eliminate(i - 1, j);
                Cell cellL = puzzle.getCells(i - 1, j);
                if (cellL.isCovered())
                    Assertions.assertFalse(false,
                            "Two covered cells cannot be next to each other in a row...");
            } else {
                puzzle.eliminate(i + 1, j);
                Cell cellR = puzzle.getCells(i + 1, j);
                if (cellR.isCovered())
                    Assertions.assertFalse(false,
                            "Two covered cells cannot be next to each other in a row...");
            }

            if (puzzle.isValidPosition(i, j - 1)) {//Second test for Constraint 2
                //Black out cells next to each out on that column
                puzzle.eliminate(i, j - 1);
                Cell cellUp = puzzle.getCells(i, j - 1);
                if (cellUp.isCovered())
                    Assertions.assertFalse(false,
                            "Two covered cells cannot be next to each other in a column...");
            } else {
                puzzle.eliminate(i, j + 1);
                Cell cellDown = puzzle.getCells(i, j + 1);
                if (cellDown.isCovered())
                    Assertions.assertFalse(false,
                            "Two covered cells cannot be next to each other in a column...");
            }

        }
    }


    /**
     * First test for Constraint 3, blacks out cells diagonally across the puzzle (0,0 to 7,7)
     *
     * @param puzzle
     */
    public void testCells1(Puzzle puzzle) {
        for (int i = 0; i < puzzle.getSize(); i++) {
            puzzle.eliminate(i, i);
        }
    }

    /**
     * Second test for Constraint 3, black out cells around chosen cell (excluding)
     * This should separate chosen cell from rest of cells on puzzle
     *
     * @param puzzle
     * @param i      index row
     * @param j      index column
     */
    public void testCells2(Puzzle puzzle, int i, int j) {
        if (!puzzle.getCells(i, j).isCovered())
            if (puzzle.isValidPosition(i - 1, j) && puzzle.isValidPosition(i + 1, j) && puzzle.isValidPosition(i, j - 1)
                    && puzzle.isValidPosition(i, j + 1)) {
                puzzle.eliminate(i - 1, j);
                puzzle.eliminate(i + 1, j);
                puzzle.eliminate(i, j - 1);
                puzzle.eliminate(i, j + 1);
            } else
                System.out.println("Error: Not an appropriate position to initiate test 2 for Constraint 3");

    }

    /**
     * Junit Test for Constraint 3. Random integers generated to form index. First test conducted (diagonal)
     * then the index received by random used for second test
     * Both tests should fail (return false)
     */
    @Test
    void testConstraint3() {
        Puzzle puzzle = new Puzzle();

        Random random = new Random();
        int i = random.nextInt(((puzzle.getSize() - 1) - 1) + 1) + 1;
        int j = random.nextInt(((puzzle.getSize() - 1) - 1) + 1) + 1;

        testCells1(puzzle);
        if (puzzle.getCells(7, 7).isCovered())
            Assertions.assertFalse(false,
                    "All white cells must be linked and cannot be closed off from each other...");
        if (puzzle.getCells(i, j).isCovered())
            puzzle.getCells(i, j).uncover();
        testCells2(puzzle, i, j);
        if ((puzzle.getCells(i - 1, j).isCovered() && puzzle.getCells(i + 1, j).isCovered() &&
                puzzle.getCells(i, j - 1).isCovered() && puzzle.getCells(i, j + 1).isCovered())) {
            Assertions.assertFalse(false, "No white cell can be separated from others...");
        }

    }

    /**
     * Testing win detection
     */
    @Test
    void testWin() {
        Puzzle puzzle = new Puzzle();
        HashSet<Integer> set = new HashSet<>();

        for (int i=0;i< puzzle.getSize();i++) {
            set.clear();
            for (int j = 0; j < puzzle.getSize(); j++) {
                if (set.contains(puzzle.getCells(i,j).getValue()))
                    puzzle.getCells(i, j).cover();
                else
                    set.add(puzzle.getCells(i,j).getValue());
            }

        }

        for (int j=0;j< puzzle.getSize();j++) {
            set.clear();
            for (int i = 0; i < puzzle.getSize(); i++) {
                if (set.contains(puzzle.getCells(i,j).getValue()))
                    puzzle.getCells(i, j).cover();
                else
                    set.add(puzzle.getCells(i,j).getValue());
            }
        }

        if(puzzle.isGameOver())
            assertTrue(true, "Recognition that game is finished");
    }


}