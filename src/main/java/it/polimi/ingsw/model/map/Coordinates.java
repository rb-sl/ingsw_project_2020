package it.polimi.ingsw.model.map;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.StrictMath.abs;

/**
 * Class managing cell positions on the real board
 */
public class Coordinates {
    /**
     * Object containing row and column of the coordinate
     */
    private final SimpleCoordinates simpleCoor;
    /**
     * Board the Coordinates is referencing to
     */
    private Board board;

    /**
     * Constructor of the Coordinates class
     * @param row coordinate's row
     * @param column coordinate's column
     * @param board board of the coordinate being created
     */
    public Coordinates(Integer row, Integer column, Board board) {
        simpleCoor = new SimpleCoordinates(row, column);
        this.board = board;
    }

    /**
     * Getter for Coordinates row
     * @return coordinate's row
     */
    public Integer getRow() {
        return simpleCoor.getRow();
    }

    /**
     * Getter for Coordinates column
     * @return coordinate's column
     */
    public Integer getColumn() {
        return simpleCoor.getColumn();
    }

    /**
     * Getter for the object containing row and column of the coordinate
     * @return row and column of the coordinate
     */
    public SimpleCoordinates getSimpleCoor() {
        return simpleCoor;
    }

    /**
     * Getter for the Coordinates' board attribute
     * @return board of this Coordinates
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Method to verify the given Coordinates are valid
     * @param row coordinate's row to be examined
     * @param column coordinate's column to be examined
     * @return true if row and column represent a valid value for a coordinate, false otherwise
     */
    private boolean isPresent(Integer row, Integer column) {
        return (row < board.getnRow() && row >= 0) && (column < board.getnCol() && column >= 0);
    }

    /**
     * Searches for coordinates adjacent to the cell
     * @return List of Coordinates adjacent to the current Worker
     */
    public List<Coordinates> getAdjacentCells() {
        ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();
        Coordinates coor = null;
        //Scanning all the adjacent coordinates, the one invoking is included
        for (Integer i = getRow() - 1; i <= getRow() + 1; i++) {
            for (Integer j = getColumn() - 1; j <= getColumn() + 1; j++) {
                if (isPresent(i, j) && !(i == getRow() && j == this.getColumn())) { //if is valid and is not the one invoking
                    coor = new Coordinates(i, j, this.board);
                    coordinates.add(coor);
                }
            }
        }
        return coordinates;
    }

    /**
     * Method to retrieve the top block placed over other blocks on this coordinate
     * @return Object MapComponent representing the top block
     */
    public MapComponent getTopBlock() {
        return board.getTopBlock(this);
    }

    /**
     * Method to retrieve the total height of the blocks stacked over this coordinate
     * @return height of the MapComponents stacked
     */
    public Integer getHeight() {
        return board.getHeight(this);
    }

    /**
     * Method to measure the difference in height between the blocks placed over this coordinate and another one
     * @param other Other Coordinates this Coordinates being compared to
     * @return height difference
     */
    public Integer heightDifference(Coordinates other) {
        Integer diff = 0;
        diff = board.getHeight(this) - board.getHeight(other);
        return diff;
    }

    /**
     * Method to add one block over the stack placed on this coordinates
     * @param blockType Type of the block to be added
     * @throws WrongBlockException Exception thrown if blockType is referencing to a block that can't exist
     */
    public void addBlockOnTop(Integer blockType) throws WrongBlockException {
        board.addMapComponent(this, blockType);
        board.addUpdate(this);
    }

    /**
     * Method to retrieve the coordinate behind the coordinates invoking this method.
     * @param other coordinate to be examined
     * @return opposed Coordinates to "other" behind the Worker. null if the opposed coordinate doesn't exist in the board
     */
    public Coordinates getOpposedOf(Coordinates other) {
        Integer diffRow = getRow() - other.getRow();
        Integer diffColumn = getColumn() - other.getColumn();
        if(isPresent(getRow() + diffRow, getColumn() + diffColumn)) {
            Coordinates opposed = new Coordinates(getRow() + diffRow, getColumn() + diffColumn, this.board);
            return opposed;
        }
        return null;
    }

    /**
     * Method to remove the block on top of the blocks stack built over this Coordinates
     * @throws WrongBlockException exception thrown when on this Coordinates there are no other block than ground
     */
    public void removeTopBlock() throws WrongBlockException {
        if (getHeight() == 0)
            throw new WrongBlockException("0");
        board.removeTopBlock(this);
        board.addUpdate(this);
    }

    /**
     * Method to establish whether a coordinate is adjacent to this Coordinate
     * @param other other coordinate to examined
     * @return true if the passed coordinate is adjacent to this Coordinate, false otherwise
     */
    public boolean isAdjacentTo(Coordinates other) {
        return abs(getRow() - other.getRow()) <= 1 && abs(this.getColumn() - other.getColumn()) <= 1;
    }

    /**
     * Method to establish whether a coordinate is on the perimeter
     * @return true if the passed coordinate is on the perimeter
     */
    public boolean isOnPerimeter() {
        return getRow().equals(board.getnRow() - 1)
                || getRow().equals(0)
                || getColumn().equals(board.getnCol() - 1)
                || getColumn().equals(0);
    }

    /**
     * Sets a worker on top of the coordinates, logging the action
     * @param worker The worker to be set
     * @throws NotFreeException If the top block is not free
     */
    public void setWorkerOnTop(Worker worker) throws NotFreeException {
        getTopBlock().setWorkerOnTop(worker);
        board.addUpdate(this);
    }

    /**
     * Removes the worker on top, logging the action
     */
    public void removeWorkerOnTop() {
        getTopBlock().removeWorkerOnTop();
        board.addUpdate(this);
    }

    /**
     * Confronts two coordinates - autogenerated
     * @param o The object to be confronted
     * @return True if the objects represent the same coordinates
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return getRow() == that.getRow() &&
                getColumn() == that.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn(), board);
    }
}
