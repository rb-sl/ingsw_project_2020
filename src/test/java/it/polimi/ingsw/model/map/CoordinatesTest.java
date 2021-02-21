package it.polimi.ingsw.model.map;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for the coordinates class
 */
public class CoordinatesTest {

    Board board;

    /**
     *  Method to test getAdjacentCells works well in the center of the board (the coordinate
     *  examined does not reference to one edge or corner of the board)
     */
    @Test
    public void getAdjacentCellsCenterTest() {
        Coordinates c;
        board = new Board(5, 5, 2);
        c = new Coordinates(2, 2, board);
        List<Coordinates> listAdjacent = c.getAdjacentCells();
        List<Coordinates> expected = new ArrayList<Coordinates>();
        expected.add(new Coordinates(1, 3, board));
        expected.add(new Coordinates(1, 2, board));
        expected.add(new Coordinates(1, 1, board));
        expected.add(new Coordinates(2, 1, board));
        expected.add(new Coordinates(3, 1, board));
        expected.add(new Coordinates(3, 2, board));
        expected.add(new Coordinates(3, 3, board));
        expected.add(new Coordinates(2, 3, board));
        assertTrue(expected.size() == listAdjacent.size());
        assertTrue(listAdjacent.contains(expected.get(0)));
        assertTrue(listAdjacent.contains(expected.get(1)));
        assertTrue(listAdjacent.contains(expected.get(2)));
        assertTrue(listAdjacent.contains(expected.get(3)));
        assertTrue(listAdjacent.contains(expected.get(4)));
        assertTrue(listAdjacent.contains(expected.get(5)));
        assertTrue(listAdjacent.contains(expected.get(6)));
        assertTrue(listAdjacent.contains(expected.get(7)));
    }

    /**
     * Method to test if getAdjacentCells works well in the edge of the board
     */
    @Test
    public void getAdjacentCellsEdgeTest() {
        Coordinates c;
        board = new Board(5, 5, 2);
        c = new Coordinates(2, 4, board);
        List<Coordinates> listAdjacent = c.getAdjacentCells();
        List<Coordinates> expected = new ArrayList<Coordinates>();
        expected.add(new Coordinates(3, 4, board));
        expected.add(new Coordinates(3, 3, board));
        expected.add(new Coordinates(2, 3, board));
        expected.add(new Coordinates(1, 3, board));
        expected.add(new Coordinates(1, 4, board));
        assertTrue(expected.size() == listAdjacent.size());
        assertTrue(listAdjacent.contains(expected.get(0)));
        assertTrue(listAdjacent.contains(expected.get(1)));
        assertTrue(listAdjacent.contains(expected.get(2)));
        assertTrue(listAdjacent.contains(expected.get(3)));
        assertTrue(listAdjacent.contains(expected.get(4)));
    }

    /**
     * Method to test if getAdjacentCells works well in the corner
     */
    @Test
    public void getAdjacentCellsCornerTest() {
        Coordinates c;
        board = new Board(5, 5, 2);
        c = new Coordinates(4, 4, board);
        List<Coordinates> listAdjacent = c.getAdjacentCells();
        List<Coordinates> expected = new ArrayList<Coordinates>();
        expected.add(new Coordinates(4, 3, board));
        expected.add(new Coordinates(3, 3, board));
        expected.add(new Coordinates(3, 4, board));
        assertTrue(expected.size() == listAdjacent.size());
        assertTrue(listAdjacent.contains(expected.get(0)));
        assertTrue(listAdjacent.contains(expected.get(1)));
        assertTrue(listAdjacent.contains(expected.get(2)));
    }

    /**
     * Method to test if heightDifference returns an expected value
     */
    @Test
    public void heightDifferenceTest() {
        board = new Board(5, 5, 2);
        Coordinates c1 = new Coordinates(3,3,board);
        Coordinates c2 = new Coordinates(3, 1, board);
        try {
            board.addMapComponent(c1, 1);
            board.addMapComponent(c1, 2);
            board.addMapComponent(c1, 3);
            board.addMapComponent(c2, 1);
            board.addMapComponent(c2, 2);
        }
        catch (WrongBlockException exception) { }
        assertTrue(c1.heightDifference(c2) == 1);
        assertTrue(c2.heightDifference(c1) == -1);
    }

    /**
     * Method tests if getOpposedOf works well when the coordinate its referencing to
     * is placed in the corner of the group of cell adjacent to the worker
     */
    @Test
    public void getOpposedOfTestDiag() {
        board = new Board(5, 5, 2);
        Coordinates c = new Coordinates(2,2, board);
        Worker w = new Worker(null, 'F', c);
        Coordinates c2 = new Coordinates(3,1, board);
        Coordinates expected = new Coordinates(1,3,board);
        assertTrue(w.getCurrentPos().getOpposedOf(c2).equals(expected));
    }


    /**
     * Method tests if getOpposedOf works well when the coordinate its referencing to
     * is placed in the left or right coordinates adjacent to the worker horizontally
     */
    @Test
    public void getOpposedOfTestHorizontal() {
        board = new Board(5, 5, 2);
        Coordinates c = new Coordinates(2,2, board);
        Worker w = new Worker(null, 'F', c);
        Coordinates c2 = new Coordinates(2,1, board);
        Coordinates expected = new Coordinates(2,3,board);
        assertTrue(w.getCurrentPos().getOpposedOf(c2).equals(expected));
    }

    /**
     * Method tests if getOpposedOf works well when the coordinate its referencing to
     * is placed in the top or bottom coordinates adjacent to the worker vertically
     */
    @Test
    public void getOpposedOfTestVertical() {
        board = new Board(5, 5, 2);
        Coordinates c = new Coordinates(2,2, board);
        Worker w = new Worker(null, 'F', c);
        Coordinates c2 = new Coordinates(1,2, board);
        Coordinates expected = new Coordinates(3,2,board);
        assertTrue(w.getCurrentPos().getOpposedOf(c2).equals(expected));
    }

    /**
     * Method tests if getOpposedOf returns null when the coordinate it should return is out of the
     * board
     */
    @Test
    public void getOpposedOfTestNonValid() {
        board = new Board(5, 5, 2);
        Coordinates c = new Coordinates(0,0, board);
        Worker w = new Worker(null, 'F', c);
        Coordinates c2 = new Coordinates(1,1, board);
        assertTrue(w.getCurrentPos().getOpposedOf(c2) == null);
    }

    /**
     * Method to check if removeTopBlock method works correctly and throws an exception when
     * the examined coordinate does not contain any block except for the ground block (level 0)
     */
    @Test
    public void removeTopBlockTest() {
        board = new Board(5, 5, 2);
        Coordinates c1 = new Coordinates(3,3,board);
        Coordinates c2 = new Coordinates(3, 1, board);
        try {
            board.addMapComponent(c1, 1);
            board.addMapComponent(c1, 2);
            board.addMapComponent(c1, 3);
            assertThrows(WrongBlockException.class, () -> c2.removeTopBlock());
            c1.removeTopBlock();
            assertTrue(c1.getHeight() == 2);
        }
        catch (WrongBlockException exception) {
            exception.printStackTrace();
        }
    }
}
