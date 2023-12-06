package hitori;

//Class with methods for each individual cell (to cover, uncover) and to check the value and its state.
public class Cell {

    private int value;
    private boolean white;

    //Sets the value of that cell and makes sure all cells are white at the beginning
    public Cell(int value) {
        this.value = value;
        white = true;
    }

    //Enable cell to be covered
    public void cover() {
        white = false;
    }

    //Enable cell to be uncovered
    public void uncover() {
        white = true;
    }

    //Check the state of cell (whether covered or not/black or white)
    public boolean isCovered() {
        return !white;
    }

    //Get the value stored in that cell
    public int getValue() {
        return value;
    }

}

