package it.polimi.ingsw.model.god;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Coordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests aim at checking the functions of the simple gods
 */
class SimpleGodsTest {
    private final Board board = new Board(5, 5, 2);
    private final Player testPlayer0 = new Player("0", board);
    private final Player testPlayer1 = new Player("1", board);
    private final Player testPlayer2 = new Player("1", board);

    /**
     * This test checks the functionality of Apollo (MoveOnOpponent + Swap)
     */
    @Test
    void apolloTest() {
        // Setup
        board.addPlayers(testPlayer0);
        board.addPlayers(testPlayer1);

        God apollo = new God("Apollo", testPlayer0);
        testPlayer0.setGod(apollo);
        God atheist = new God("Atheist", testPlayer1);
        testPlayer1.setGod(atheist);

        try {
            apollo.decorateMovement("MoveOnOpponent");
            apollo.decorateMovement("Swap");
            apollo.decorateMovement("CanMoveAndBuild");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerCoordinates = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerCoordinates);
        try {
            workerCoordinates.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        Worker opponentWorker = new Worker(testPlayer1, 'F');
        Coordinates opponentCoordinates = new Coordinates(0,1, board);
        opponentWorker.setCurrentPosition(opponentCoordinates);
        try {
            opponentCoordinates.getTopBlock().setWorkerOnTop(opponentWorker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        assertFalse(opponentWorker.reachableCells().contains(worker.getCurrentPosition()));

        // Checks MoveOnOpponent
        assertTrue(apollo.getMove().canReach(worker, opponentCoordinates));

        // Checks Swap
        try {
            worker.moveTo(new Coordinates(0,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        assertTrue(worker.getCurrentPosition().equals(new Coordinates(0,1, board)));
        assertTrue(opponentWorker.getCurrentPosition().equals(new Coordinates(0,0, board)));

        assertTrue(!worker.buildableCells().isEmpty());
    }

    /**
     * This test checks the functionality of Artemis (moveableTimes = 2 + NotOnPreviousPosition)
     */
    @Test
    void artemisTest() {
        // Setup
        board.addPlayers(testPlayer0);

        God artemis = new God("Artemis", testPlayer0);
        testPlayer0.setGod(artemis);

        try {
            artemis.setMovableTimes(2);
            artemis.decorateMovement("NotOnPreviousPosition");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerCoordinates = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerCoordinates);
        try {
            workerCoordinates.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        try {
            worker.moveTo(new Coordinates(1,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        // Checks multiple moves
        assertTrue(artemis.getMove().canReach(worker, new Coordinates(2,2, board)));

        // Checks NotOnPreviousPosition
        assertTrue(!artemis.getMove().canReach(worker, new Coordinates(1,1, board)));

        // Checks that the movement is blocked
        assertThrows(CantReachException.class, () -> artemis.getMove().moveTo(worker, new Coordinates(1,1, board)));
    }

    /**
     * This test checks the functionality of Athena (InhibitMoveUp + triggers opponents)
     */
    @Test
    void athenaTest() {
        // Setup
        board.addPlayers(testPlayer0);
        board.addPlayers(testPlayer1);
        board.addPlayers(testPlayer2);

        God athena = new God("Athena", testPlayer0);
        testPlayer0.setGod(athena);
        God atheist1 = new God("Atheist1", testPlayer1);
        testPlayer1.setGod(atheist1);
        God atheist2 = new God("Atheist2", testPlayer2);
        testPlayer2.setGod(atheist2);

        try {
            athena.decorateMovement("InhibitMoveUp");
            athena.getEventManager().subscribe("CantMoveUp",atheist1);
            athena.getEventManager().subscribe("CantMoveUp",atheist2);

            atheist1.decorateMovement("CantMoveUp");
            atheist2.decorateMovement("CantMoveUp");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerCoordinates = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerCoordinates);
        try {
            workerCoordinates.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        Worker opponentWorker1 = new Worker(testPlayer1, 'F' );
        Coordinates opponentCoordinates1 = new Coordinates(3,4, board);
        opponentWorker1.setCurrentPosition(opponentCoordinates1);
        try {
            opponentCoordinates1.getTopBlock().setWorkerOnTop(opponentWorker1);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        Worker opponentWorker2 = new Worker(testPlayer2, 'F' );
        Coordinates opponentCoordinates2 = new Coordinates(3,3, board);
        opponentWorker2.setCurrentPosition(opponentCoordinates2);
        try {
            opponentCoordinates2.getTopBlock().setWorkerOnTop(opponentWorker2);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        Coordinates target = new Coordinates(1,0, board);
        Coordinates opponentTarget = new Coordinates(2,3, board);

        try {
            target.addBlockOnTop(1);
            opponentTarget.addBlockOnTop(1);
        } catch (WrongBlockException e) {
            e.printStackTrace();
        }

        // Checks that opponent workers can normally move up in normal conditions
        try {
            athena.getMove().moveTo(worker, new Coordinates(1,1, board));
        } catch (HasWonException | CantReachException | NotFreeException e) {
            e.printStackTrace();
        }

        assertTrue(atheist1.getMove().canReach(opponentWorker1, opponentTarget));
        assertTrue(atheist2.getMove().canReach(opponentWorker2, opponentTarget));

        worker.setTimesMoved(0);

        // Checks that a moveup inhibits the opponent workers' ability to move up
        try {
            worker.moveTo(target);
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        assertTrue(atheist1.isTriggered("CantMoveUp"));

        assertTrue(!atheist1.getMove().canReach(opponentWorker1, opponentTarget));
        assertTrue(!atheist2.getMove().canReach(opponentWorker2, opponentTarget));

        // Checks that after the turn other workers can normally move up
        atheist1.resetTriggers();
        atheist2.resetTriggers();

        assertTrue(atheist1.getMove().canReach(opponentWorker1, opponentTarget));
        assertTrue(atheist2.getMove().canReach(opponentWorker2, opponentTarget));
    }

    /**
     * This test checks the functionality of Atlas (DomeEverywhere)
     */
    @Test
    void atlasTest() {
        // Setup
        board.addPlayers(testPlayer0);

        God atlas = new God("Atlas", testPlayer0);
        testPlayer0.setGod(atlas);

        try {
            atlas.decorateBuild("DomeEverywhere");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerOrigin = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerOrigin);
        try {
            workerOrigin.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        try {
            worker.moveTo(new Coordinates(0,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        // Checks the possibility to build a dome at level 0
        assertTrue(!atlas.getBuild().canBuild(worker,workerOrigin).isEmpty());

        // Builds a level 0 dome and checks the correctness of the block
        try {
            atlas.getBuild().buildTo(worker, workerOrigin, 4);
        } catch (WrongBlockException | CantBuildException e) {
            e.printStackTrace();
        }

        assertTrue(workerOrigin.getTopBlock().getLevel() == 4 && workerOrigin.getHeight() == 1);
    }

    /**
     * This test checks the functionality of Demeter (buildableTimes = 2 + NotOnPreviousBuild)
     */
    @Test
    void demeterTest() {
        // Setup
        board.addPlayers(testPlayer0);

        God demeter = new God("Demeter", testPlayer0);
        testPlayer0.setGod(demeter);

        try {
            demeter.setBuildableTimes(2);
            demeter.decorateBuild("NotOnPreviousBuild");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerOrigin = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerOrigin);
        try {
            workerOrigin.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        try {
            worker.moveTo(new Coordinates(0,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        Coordinates targetCoordinates = new Coordinates(0,2, board);

        worker.buildTo(targetCoordinates, targetCoordinates.getHeight() + 1);

        // Checks the possibility to build again, first on other coordinates then on the same
        assertTrue(!demeter.getBuild().canBuild(worker, new Coordinates(1,1, board)).isEmpty());
        assertTrue(demeter.getBuild().canBuild(worker, targetCoordinates).isEmpty());

        // Checks that the previous build is blocked
        assertThrows(CantBuildException.class, () -> demeter.getBuild().buildTo(worker, targetCoordinates, targetCoordinates.getHeight() + 1));
    }

    /**
     * This test checks the functionality of Hephaestus (buildableTimes = 2 + OnPreviousBuild + NoDomeOnSecond)
     */
    @Test
    void hephaestusTest() {
        // Setup
        board.addPlayers(testPlayer0);

        God hephaestus = new God("Hephaestus", testPlayer0);
        testPlayer0.setGod(hephaestus);

        try {
            hephaestus.setBuildableTimes(2);
            hephaestus.decorateBuild("NoDomeOnSecond");
            hephaestus.decorateBuild("OnPreviousBuild");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerOrigin = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerOrigin);
        try {
            workerOrigin.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        try {
            worker.moveTo(new Coordinates(0,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        Coordinates target = new Coordinates(0,2, board);

        worker.buildTo(target,target.getHeight() + 1);

        // Checks that the worker can build again on the same space, but not on others
        assertTrue(hephaestus.getBuild().canBuild(worker, target).contains(2));
        assertTrue(hephaestus.getBuild().canBuild(worker, new Coordinates(1,1, board)).isEmpty());

        worker.buildTo(target,target.getHeight() + 1);

        worker.setTimesBuilt(0);

        worker.buildTo(target,target.getHeight() + 1);
        // Checks that the worker cannot build a dome as second build
        assertTrue(hephaestus.getBuild().canBuild(worker, new Coordinates(0, 2, board)).isEmpty());

        // Checks that the worker can normally build a dome
        worker.setTimesBuilt(0);
        assertTrue(hephaestus.getBuild().canBuild(worker, new Coordinates(0,2, board)).contains(4));
    }

    /**
     * This test checks the functionality of Minotaur (MoveOnOpponent + Bump)
     */
    @Test
    void minotaurTest() {
        // Setup
        board.addPlayers(testPlayer0);
        board.addPlayers(testPlayer1);

        God minotaur = new God("Minotaur", testPlayer0);
        testPlayer0.setGod(minotaur);
        God atheist = new God("Atheist", testPlayer1);
        testPlayer1.setGod(atheist);

        try {
            minotaur.decorateMovement("MoveOnOpponent");
            minotaur.decorateMovement("Bump");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerCoordinates = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerCoordinates);
        try {
            workerCoordinates.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        Worker opponentWorker = new Worker(testPlayer1, 'F' );
        Coordinates opponentCoordinates = new Coordinates(0,1, board);
        opponentWorker.setCurrentPosition(opponentCoordinates);
        try {
            opponentCoordinates.getTopBlock().setWorkerOnTop(opponentWorker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        // Checks MoveOnOpponent
        assertTrue(minotaur.getMove().canReach(worker, opponentCoordinates));

        try {
            worker.moveTo(opponentCoordinates);
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        // Checks the correct execution of Bump
        assertTrue(worker.getCurrentPosition().equals(new Coordinates(0,1, board)));
        assertTrue(opponentWorker.getCurrentPosition().equals(new Coordinates(0,2, board)));

        worker.setTimesMoved(0);

        // Moves the opponent worker to a cell from which it cannot be bumped, then tests the failure of the move
        (new Coordinates(0,2, board)).getTopBlock().removeWorkerOnTop();
        opponentWorker.setCurrentPosition(new Coordinates(0,0, board));
        try {
            (new Coordinates(0,0, board)).getTopBlock().setWorkerOnTop(opponentWorker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        assertTrue(!minotaur.getMove().canReach(worker, new Coordinates(0,0, board)));
        assertThrows(CantReachException.class, () -> worker.getPlayer().getGod().getMove().moveTo(worker, new Coordinates(0,0, board)));
    }

    /**
     * This test checks the functionality of Pan (Jump2)
     */
    @Test
    void panTest() {
        // Setup
        board.addPlayers(testPlayer0);
        God pan = new God("Pan", testPlayer0);
        testPlayer0.setGod(pan);

        try {
            pan.decorateWin("Jump2");
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }

        Coordinates high = new Coordinates(0, 0, board);
        try {
            high.addBlockOnTop(1);
            high.addBlockOnTop(2);
        } catch(WrongBlockException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'m', high);
        try {
            high.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        // Checks that the player wins when moving down 2 or more levels too
        assertTrue(high.getHeight() == 2);
        assertTrue(worker.getCurrentPosition().equals(high));

        assertThrows(HasWonException.class, () -> worker.moveTo(new Coordinates(1,0, board)));
    }

    /**
     * This test checks the functionality of Prometheus (CantMoveUp, BuildBeforeMove)
     */
    @Test
    void prometheusTest() {
        // Setup
        board.addPlayers(testPlayer0);

        God prometheus = new God("Prometheus", testPlayer0);
        testPlayer0.setGod(prometheus);

        try {
            prometheus.setBuildableTimes(2);
            prometheus.decorateMovement("MoveAfterBuild");
            prometheus.decorateMovement("CantMoveUp");
            prometheus.getEventManager().subscribe("CantMoveUp", prometheus);
            prometheus.decorateBuild("BuildBeforeMove");
            prometheus.decorateBuild("CanBuildAndMove");
        } catch(BadConfigurationException e) {
            e.printStackTrace();
        }

        Worker worker = new Worker(testPlayer0,'M');
        Coordinates workerCoordinates = new Coordinates(0,0, board);
        worker.setCurrentPosition(workerCoordinates);
        try {
            workerCoordinates.getTopBlock().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        Coordinates up = new Coordinates(1,0, board);
        try {
            up.addBlockOnTop(1);
        } catch (WrongBlockException e) {
            e.printStackTrace();
        }

        // Checks that the worker can build before moving
        assertTrue(!prometheus.getBuild().canBuild(worker, (new Coordinates(1,1,board))).isEmpty());

        worker.buildTo(new Coordinates(1,1, board), 1);

        //Checks that the worker cannot build again without moving
        assertTrue(prometheus.getBuild().canBuild(worker, (new Coordinates(0,1,board))).isEmpty());

        // Checks that the worker cannot move up after a build before moving
        assertTrue(prometheus.getMove().canReach(worker, new Coordinates(0,1, board)));
        assertFalse(prometheus.getMove().canReach(worker, up));
        assertThrows(CantReachException.class, () -> prometheus.getMove().moveTo(worker,up));

        // Checks that the worker can build again after moving
        try {
            worker.moveTo(new Coordinates(0,1, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        assertTrue(!prometheus.getBuild().canBuild(worker, new Coordinates(0,2, board)).isEmpty());

        // New turn
        prometheus.resetTriggers();
        worker.setTimesMoved(0);
        worker.setTimesBuilt(0);

        try {
            worker.moveTo(new Coordinates(0,2, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }

        prometheus.getBuild().canBuild(worker, new Coordinates(0,3, board));
        worker.buildTo(new Coordinates(0,3, board),1);

        // Checks that the worker can build only one time after moving
        assertTrue(prometheus.getBuild().canBuild(worker, new Coordinates(1,2, board)).isEmpty());
    }
}