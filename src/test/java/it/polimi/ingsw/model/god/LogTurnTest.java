package it.polimi.ingsw.model.god;

import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.log.LogTurn;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Coordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests aim at checking the correctness of the logging components (included AF undo)
 */
class LogTurnTest {
    private LogTurn logTurn = new LogTurn();
    private static final String event = "Event";
    private Board board = new Board(5,5, 2);
    private Player player = new Player("player", board);
    God god = new God("god", player);
    private GodEventManager eventManager = new GodEventManager(god);
    private Worker worker = new Worker(player,'m');

    /**
     * Test checking the correct adding of actions into a log
     */
    @Test
    void LogTest() {
        board.addPlayers(player);
        player.addWorker(worker);
        Coordinates workerCoordinates = new Coordinates(1,1, board);
        worker.setCurrentPosition(workerCoordinates);
        try {
            workerCoordinates.setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        logTurn.addPlaceWorker(workerCoordinates);

        logTurn.undo();

        assertTrue(player.getWorkers().isEmpty());
        assertTrue(!workerCoordinates.getTopBlock().hasWorkerOnTop());

        worker.setCurrentPosition(workerCoordinates);

        logTurn.addMove(LogTurn.order.MAIN, worker, worker.getCurrentPosition());

        assertTrue(logTurn.getLatestCoordinates().equals(new Coordinates(1,1, board)));

        logTurn.addBuild(LogTurn.order.MAIN, worker, new Coordinates(1,3, board));
        try{
            (new Coordinates(1,3, board)).addBlockOnTop(1);
        } catch(WrongBlockException e){
            e.printStackTrace();
        }

        logTurn.addEvent(LogTurn.order.AFTER, eventManager, event, 1);

        assertTrue(logTurn.getLatestCoordinates().equals(new Coordinates(1,3, board)));
    }

    /**
     * Test verifying the undo of an action concerning multiple subactions that need multithreading to end
     */
    @Test
    void UndoTest() {
        Player player2 = new Player("player2", board);
        player.setGod(god);
        board.addPlayers(player);
        board.addPlayers(player2);

        worker.setCurrentPosition(new Coordinates(1,1, board));
        Worker e1 = new Worker(player2,'a', new Coordinates(1,0, board));
        Worker e2 = new Worker(player2,'b', new Coordinates(0,1, board));

        try {
            worker.getCurrentPosition().getTopBlock().setWorkerOnTop(worker);
            e1.getCurrentPosition().getTopBlock().setWorkerOnTop(e1);
            e2.getCurrentPosition().getTopBlock().setWorkerOnTop(e2);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        try {
            worker.moveTo(new Coordinates(0,0, board));
        } catch (HasWonException e) {
            e.printStackTrace();
        }
        logTurn.addMove(LogTurn.order.MAIN, worker, worker.getPreviousPosition());

        logTurn.addMove(LogTurn.order.AFTER, e1, e1.getCurrentPosition());
        logTurn.addMove(LogTurn.order.AFTER, e2, e2.getCurrentPosition());
        try {
            e2.getCurrentPosition().getTopBlock().removeWorkerOnTop();
            e1.forceTo(new Coordinates(0,1, board));
            e2.forceTo(new Coordinates(1,0, board));
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        Coordinates buildTarget = (new Coordinates(1,1, board));
        try {
            buildTarget.addBlockOnTop(1);
            buildTarget.addBlockOnTop(2);
        } catch (WrongBlockException e) {
            e.printStackTrace();
        }
        logTurn.addBuild(LogTurn.order.MAIN, e1, buildTarget);
        logTurn.addBuild(LogTurn.order.AFTER, e1, buildTarget);

        assertTrue(buildTarget.getHeight() == 2);

        logTurn.undoAll();

        assertTrue(e1.getCurrentPosition().equals(new Coordinates(1,0, board)));
        assertTrue(e2.getCurrentPosition().equals(new Coordinates(0,1, board)));
        assertTrue(buildTarget.getHeight() == 0);
    }
}