package it.polimi.ingsw.model.map;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for the Player class
 */
public class PlayerTest {
    /**
     * Method to check if the list and its related methods work correctly
     */
    @Test
    public void WorkerList () {
        Board board = new Board(5,5, 2);
        Player player = new Player("Gregor", board);
        Worker w1 = new Worker(player, 'M');
        Worker w2 = new Worker(player, 'F');
        player.addWorker(w1);
        player.addWorker(w2);
        assertTrue(player.getWorkers().size() == 2);
        List<Worker> list = player.getWorkers();
        //The internal list should not be modified from the outside
        list.remove(0);
        assertTrue(player.getWorkers().size() == 2);
        player.addWorker(null);
        assertTrue(player.getWorkers().size() == 2);
        player.removeWorker(w1);
        assertTrue(player.getWorkers().size() == 1);
        player.addWorker(w1);
        player.removeWorker(1);
        assertTrue(player.getWorkers().size() == 1);
        assertTrue(player.getWorker(0).equals(w2));
        //Check the index control
        player.removeWorker(-1);
        assertTrue(player.getWorker(-1) == null);
        assertTrue(player.getWorkers().size() == 1);
    }
}
