package it.polimi.ingsw.model.god;

import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Coordinates;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests concerning the developed advanced gods
 */
public class AdvancedGodsTest {
    private final Board board = new Board(5, 5, 2);
    private final Player testPlayer0 = new Player("0", board);
    private final Player testPlayer1 = new Player("1", board);
    private final Player testPlayer2 = new Player("1", board);

    /**
     * Test checking the functionalities related to Hera (InhibitPerimeterWin)
     */
    @Test
    public void heraTest() {
        // Setup
        board.addPlayers(testPlayer0);
        board.addPlayers(testPlayer1);
        God hera = new God("Hera", testPlayer0);
        testPlayer0.setGod(hera);
        God atheist = new God("Atheist", testPlayer1);
        testPlayer1.setGod(atheist);

        try {
            for(God god: hera.getPlayer().getOpponents().stream().map(Player::getGod).collect(Collectors.toList())) {
                god.decorateWin("NotOnPerimeter");
            }
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }

        Coordinates highPerimeter = new Coordinates(0, 0, board);
        Coordinates highNoPerimeter = new Coordinates(1, 1, board);
        Coordinates height2 = new Coordinates(0,1, board);
        try {
            highPerimeter.addBlockOnTop(1);
            highPerimeter.addBlockOnTop(2);
            highPerimeter.addBlockOnTop(3);

            highNoPerimeter.addBlockOnTop(1);
            highNoPerimeter.addBlockOnTop(2);
            highNoPerimeter.addBlockOnTop(3);

            height2.addBlockOnTop(1);
            height2.addBlockOnTop(2);
        } catch(WrongBlockException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer1,'m', height2);
        try {
            height2.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        // Checks that the opponent can win by moving onto a non-perimeter space
        assertThrows(HasWonException.class, () -> worker.moveTo(new Coordinates(1,1, board)));

        atheist.getLogTurn().undo();

        // Checks that no exception is thrown when moving to a perimeter third level
        try {
            worker.moveTo(new Coordinates(0,0, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test checking the functionalities related to Hestia (NoPerimeterOnSecond + can build twice)
     */
    @Test
    public void hestiaTest() {
        // Setup
        board.addPlayers(testPlayer0);
        God hestia = new God("Hera", testPlayer0);
        testPlayer0.setGod(hestia);

        try {
            hestia.setBuildableTimes(2);
            hestia.decorateBuild("NoPerimeterOnSecond");
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }

        Coordinates workerPosition = new Coordinates(0,0, board);
        Worker worker = new Worker(testPlayer0,'m', workerPosition);
        try {
            workerPosition.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        try {
            worker.moveTo(new Coordinates(1,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        worker.buildTo(new Coordinates(2,2, board), 1);

        assertTrue(!hestia.getBuild().canBuild(worker, new Coordinates(1,2, board)).isEmpty());
        assertTrue(hestia.getBuild().canBuild(worker, new Coordinates(0,0, board)).isEmpty());
    }

    /**
     * Test checking the functionalities related to Hypnus (InhibitMoveHighest)
     */
    @Test
    public void hypnusTest() {
        // Setup
        board.addPlayers(testPlayer0);
        board.addPlayers(testPlayer1);
        God hypnus = new God("Hypnus", testPlayer0);
        testPlayer0.setGod(hypnus);
        God atheist = new God("Atheist", testPlayer1);
        testPlayer1.setGod(atheist);

        try {
            for(God god: hypnus.getPlayer().getOpponents().stream().map(Player::getGod).collect(Collectors.toList())) {
                god.decorateMovement("CantMoveHighest");
            }
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }

        Coordinates worker1Position = new Coordinates(0,0, board);
        Worker worker1 = new Worker(testPlayer1,'m', worker1Position);
        try {
            worker1Position.getTopBlock().setWorkerOnTop(worker1);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }
        testPlayer1.addWorker(worker1);

        Coordinates worker2Position = new Coordinates(0,1, board);
        Worker worker2 = new Worker(testPlayer1,'f', worker2Position);
        try {
            worker2Position.getTopBlock().setWorkerOnTop(worker2);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }
        testPlayer1.addWorker(worker2);

        // checks that both can move
        assertTrue(atheist.getMove().canReach(worker1, new Coordinates(1,1, board)));
        assertTrue(atheist.getMove().canReach(worker2, new Coordinates(1,1, board)));

        // moves w1 higher
        worker1Position.getTopBlock().removeWorkerOnTop();
        try {
            worker1Position.addBlockOnTop(1);
        } catch (WrongBlockException e) {
            e.printStackTrace();
        }
        try {
            worker1Position.getTopBlock().setWorkerOnTop(worker1);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        // checks that worker2 can still move, but worker1 can't
        assertTrue(!atheist.getMove().canReach(worker1, new Coordinates(1,1, board)));
        assertTrue(atheist.getMove().canReach(worker2, new Coordinates(1,1, board)));
    }

    /**
     * Test checking the functionalities related to Limus (Inhibits builds near its workers)
     */
    @Test
    public void limusTest() {
        // Setup
        board.addPlayers(testPlayer0);
        board.addPlayers(testPlayer1);
        God limus = new God("Limus", testPlayer0);
        testPlayer0.setGod(limus);
        God atheist = new God("Atheist", testPlayer1);
        testPlayer1.setGod(atheist);

        try {
            limus.getEventManager().subscribe("NoBlockNearWorker",limus);
            for(God god: limus.getPlayer().getOpponents().stream().map(Player::getGod).collect(Collectors.toList())) {
                god.decorateBuild("NoBlockNearWorker");
            }
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }

        Coordinates workerPosition = new Coordinates(0,1, board);
        Worker worker = new Worker(testPlayer0,'m', workerPosition);
        try {
            workerPosition.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }
        testPlayer0.addWorker(worker);

        Coordinates opponentPosition = new Coordinates(1,1, board);
        Worker opponentWorker = new Worker(testPlayer1,'f', opponentPosition);
        try {
            opponentPosition.getTopBlock().setWorkerOnTop(opponentWorker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }
        testPlayer1.addWorker(opponentWorker);
        opponentWorker.setTimesMoved(1);


        // checks that the opponent cannot build near limus's worker (if not a dome), but can in other positions
        assertTrue(!atheist.getBuild().canBuild(opponentWorker, new Coordinates(2,2, board)).isEmpty());
        Coordinates nearLimus = new Coordinates(0,0, board);
        assertTrue(atheist.getBuild().canBuild(opponentWorker, nearLimus).isEmpty());

        try {
            nearLimus.addBlockOnTop(1);
            nearLimus.addBlockOnTop(2);
            nearLimus.addBlockOnTop(3);
        } catch (WrongBlockException e) {
            e.printStackTrace();
        }
        //checks that a dome can still be built near limus
        assertTrue(!atheist.getBuild().canBuild(opponentWorker, nearLimus).isEmpty());
    }

    /**
     * Test checking the functionalities related to Triton (Can move indefinitely on the perimeter)
     */
    @Test
    public void tritonTest() {
        // Setup
        board.addPlayers(testPlayer0);
        God triton = new God("Triton", testPlayer0);
        testPlayer0.setGod(triton);

        try {
            triton.decorateMovement("IndefinitelyOnPerimeter");
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }

        Coordinates workerPosition = new Coordinates(1,1, board);
        Worker worker = new Worker(testPlayer0,'m', workerPosition);
        try {
            workerPosition.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }
        testPlayer0.addWorker(worker);

        try {
            worker.moveTo(new Coordinates(0,0, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        // checks that triton can move again, both on perimeter or not
        assertTrue(triton.getMove().canReach(worker, new Coordinates(1,1, board)));
        assertTrue(triton.getMove().canReach(worker, new Coordinates(0,1, board)));

        // moves an arbitrary number of times on the perimeter, then on a non perimeter space
        try {
            worker.moveTo(new Coordinates(0,1, board));
            worker.moveTo(new Coordinates(1,0, board));
            worker.moveTo(new Coordinates(2,0, board));

            worker.moveTo(new Coordinates(2,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        // checks that triton cannot move anymore, even on perimeter
        assertTrue(!triton.getMove().canReach(worker, new Coordinates(2,2, board)));
        assertTrue(!triton.getMove().canReach(worker, new Coordinates(2,0, board)));
    }
}
