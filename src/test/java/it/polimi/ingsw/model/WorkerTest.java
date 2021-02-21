package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.god.God;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Coordinates;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Worker class
 */
public class WorkerTest {
    /**
     * Method to check if reachableCells and buildableCells method works correctly and the respective lists they
     * return contain the expected Coordinates entries. It checks domes, another worker and a level superior to the
     * one reachable by the worker are excluded from the list while a level one block position is contained.
     */
    @Test
    public void validCellsTest() {
        Board board = new Board(5,5, 2);
        Player player = new Player("Donald Duck", board);
        God god = new God("God", player);
        god.setMovableTimes(2);
        player.setGod(god);
        Coordinates cr_22 = new Coordinates(2,2, board);
        Coordinates cr_11 = new Coordinates(1,1, board);
        Coordinates cr_23 = new Coordinates(2, 3, board);
        Coordinates cr_32 = new Coordinates(3,2, board);
        Coordinates cr_21 = new Coordinates(2,1,board);
        Worker w = new Worker(player, 'F', cr_22);
        Worker w2 = new Worker(player, 'M', cr_32);
        try {
            board.addMapComponent(cr_11, 1);
            board.addMapComponent(cr_23, 4);
            board.addMapComponent(cr_21, 1);
            board.addMapComponent(cr_21, 2);
            cr_32.getTopBlock().setWorkerOnTop(w2);
        }
        catch(WrongBlockException | NotFreeException exception) {}
        List<Coordinates> reachables = w.reachableCells();
        try {
            w.moveTo(cr_11);
            w.moveTo(cr_22);
        }
        catch(HasWonException exception) {
            exception.printStackTrace();
        }
        Map<Coordinates, List<Integer>> buildables = w.buildableCells();
        //Check all the reachable cells are contained in the list
        assertTrue(reachables.contains(new Coordinates(1,1, board)));
        assertTrue(reachables.contains(new Coordinates(3,1, board)));
        assertTrue(reachables.contains(new Coordinates(1,2, board)));
        assertTrue(reachables.contains(new Coordinates(1,3, board)));
        assertTrue(reachables.contains(new Coordinates(3,3, board)));
        //Check all the buildable cells are contained in the list
        List<Integer> integers = buildables.get(new Coordinates(1,1, board));
        assertTrue(!buildables.get(new Coordinates(1,1, board)).isEmpty());
        assertTrue(!buildables.get(new Coordinates(2,1, board)).isEmpty());
        assertTrue(!buildables.get(new Coordinates(3,1, board)).isEmpty());
        assertTrue(!buildables.get(new Coordinates(1,2, board)).isEmpty());
        assertTrue(!buildables.get(new Coordinates(1,3, board)).isEmpty());
        assertTrue(!buildables.get(new Coordinates(3,3, board)).isEmpty());
        assertTrue(reachables.size() == 5);
        assertTrue(buildables.size() == 6);
    }

    /**
     * Method to check if moveTo and buildTo methods in worker throw exception when they must to and checks
     * correctly if the worker can move or not to the passed coordinate
     */
    @Test
    public void moveAndBuildTest() {
        Board board = new Board(5,5, 2);
        Player player = new Player("Donald Duck", board);
        //Generic god not present in the game
        God god = new God("God", player);
        god.setMovableTimes(8);
        god.setBuildableTimes(5);
        player.setGod(god);
        Coordinates cr_22 = new Coordinates(2,2, board);
        Coordinates cr_11 = new Coordinates(1,1, board);
        Coordinates cr_12 = new Coordinates(1, 2, board);
        Coordinates cr_32 = new Coordinates(3,2, board);
        Coordinates cr_13 = new Coordinates(1,3, board);
        Coordinates cr_33 = new Coordinates(3,3, board);
        Coordinates cr_23 = new Coordinates(2,3, board);
        Worker w = new Worker(player, 'F', cr_22);
        player.addWorker(w);
        Worker w2 = new Worker(player, 'M', cr_32);
        player.addWorker(w2);
        try {
            cr_22.getTopBlock().setWorkerOnTop(w);
            cr_32.getTopBlock().setWorkerOnTop(w2);
        } catch(NotFreeException exception) {
            exception.printStackTrace();
        }
        try {
            //initialize map for test
            board.addMapComponent(cr_11, 1);
            board.addMapComponent(cr_12, 4);
            board.addMapComponent(cr_33, 1);
            board.addMapComponent(cr_23, 1);
            board.addMapComponent(cr_23, 2);
            w.moveTo(cr_11);
            w.moveTo(cr_22);
            w.buildTo(cr_13,1);
            //Worker should not be able to built over a dome in cr_12
            w.buildTo(cr_12,4);
            assertTrue(w.getTimesBuilt() == 1);
            w.buildTo(cr_13,2);
            w.buildTo(cr_13,3);
            assertTrue(w.getTimesBuilt() < w.getPlayer().getGod().getBuildableTimes());
        }
        catch(WrongBlockException | HasWonException exception) {
            exception.printStackTrace();
        }
        try {
            w.setTimesBuilt(0);
            w.moveTo(cr_11);
            w.moveTo(cr_22);
            w.moveTo(cr_33);
            w.moveTo(cr_23);
            //Worker should not be able to move on a dome
            w.moveTo(cr_12);
            assertTrue(w.getTimesMoved() == 6);
            assertTrue(w.getTimesMoved() < w.getPlayer().getGod().getMovableTimes());
            assertTrue(w.getCurrentPosition().equals(cr_23));
            //Checking the previous position
            assertTrue(w.getPreviousPos().equals(cr_33));
            assertTrue(cr_13.getTopBlock().getLevel() == 3);
            //Worker should win if it goes the 3rd level
            assertThrows(HasWonException.class, () -> w.moveTo(cr_13));
            w.moveTo(cr_23);
            assertTrue(w.getTimesMoved().equals(w.getPlayer().getGod().getMovableTimes()));
        }
        catch(HasWonException exception) {
            exception.printStackTrace();
        }
        w.buildTo(cr_33, 2);
        assertTrue(cr_33.getTopBlock().getLevel() == 2);
        w.buildTo(cr_33, 4);
        //Worker should not build a dome if it doesn't have Atlas power
        assertFalse(cr_33.getTopBlock().getLevel() == 4);
        //Should not be executed
        w.buildTo(cr_11, 4);
        assertTrue(cr_11.getTopBlock().getLevel() == 1);
    }

    /**
     * Method to check if forceTo updates manages the update of the current position, previous position and the status
     * of the current block and the previous block the worker targeted by the worker.
     */
    @Test
    public void forceToTest() {
        Board board = new Board(5,5, 2);
        Player player = new Player("Donald Duck", board);
        Coordinates cr_22 = new Coordinates(2,2, board);
        Worker w = new Worker(player, 'F', cr_22);
        Coordinates cr_11 = new Coordinates(1,1, board);
        Coordinates cr_12 = new Coordinates(1, 2, board);
        Coordinates cr_23 = new Coordinates(2,3, board);
        try {
            cr_22.getTopBlock().setWorkerOnTop(w);
        } catch(NotFreeException exception) {}
        try {
            board.addMapComponent(cr_11, 1);
            board.addMapComponent(cr_12, 4);
            board.addMapComponent(cr_23, 1);
            board.addMapComponent(cr_23, 2);
        }
        catch(WrongBlockException exception) {
            exception.printStackTrace();
        }
        try {
            w.forceTo(cr_11);
            //Worker cannot be force onto a dome
            assertThrows(NotFreeException.class, () -> w.forceTo(cr_12));
            w.forceTo(cr_23);
            //Checking positions and block status are update in the right way
            assertTrue(w.getPreviousPos().equals(cr_11));
            assertTrue(w.getCurrentPosition().equals(cr_23));
            assertTrue(cr_11.getTopBlock().getWorkerOnTop() == null);
            assertTrue(cr_23.getTopBlock().getWorkerOnTop().equals(w));
        }
        catch(NotFreeException exception) {
            exception.printStackTrace();
        }
    }
}
