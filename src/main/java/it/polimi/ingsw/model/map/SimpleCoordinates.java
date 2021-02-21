package it.polimi.ingsw.model.map;

import java.util.Objects;

/**
 * Class containing only the row and column of Coordinates objects. It represents the structure of a real 2-dimensional
 * coordinate.
 */
public class SimpleCoordinates {
    /**
     * Row of the coordinate
     */
    private final Integer row;
    /**
     * Column of the coordinate
     */
    private final Integer column;

    /**
     * Constructor for the SimpleCoordinates class
     * @param row row of the Coordinate object
     * @param column column of the Coordinate object
     */
    public SimpleCoordinates(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Getter for the row of the SimpleCoordinates instance
     * @return row of the coordinate
     */
    public Integer getRow() {
        return row;
    }

    /**
     * Getter for the column of the SimpleCoordinates instance
     * @return column of the coordinate
     */
    public Integer getColumn() {
        return column;
    }

    /**
     * Confronts two SimpleCoordinates
     * @param o The object to be compared
     * @return True if the two objects represent the same SimpleCoordinates
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleCoordinates that = (SimpleCoordinates) o;
        return row.equals(that.row) &&
                column.equals(that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
