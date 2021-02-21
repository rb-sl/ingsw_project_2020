package it.polimi.ingsw.model.god.log;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.List;

/**
 * Class used to log the change of position of a worker
 */
public class LogMove extends LogAction {
    /**
     * Object used to synchronize the revert of multiple moves
     */
    private static final Object moveLock = new Object();
    /**
     * The worker connected to the action
     */
    private final Worker worker;

    /**
     * Constructor for the class
     * @param worker The worker which was moved
     * @param previousCoordinates The coordinates the worker left
     */
    public LogMove(Worker worker, Coordinates previousCoordinates) {
        super.setType(actionType.MOVE);
        this.worker = worker;
        super.setCoordinates(previousCoordinates);
    }

    /**
     * Overloaded constructor, specifies a list of subactions
     * @param worker The worker which was moved
     * @param previousCoordinates The coordinates the worker left
     * @param subActions The list of connected subactions
     */
    public LogMove(Worker worker, Coordinates previousCoordinates, List<LogAction> subActions) {
        this(worker, previousCoordinates);
        super.setSubActions(subActions);
    }

    /**
     * Reverts the change of position of a worker and all its connected subactions
     */
    @Override
    public void revert() {
        List<Thread> threadList = super.revertSubActions();

        synchronized (moveLock) {
            worker.getCurrentPosition().removeWorkerOnTop();
            moveLock.notifyAll();
            worker.setCurrentPosition(super.getCoordinates());
            // If the worker's previous position is occupied, the method waits for one of its threads
            // to free the position
            while (!worker.getCurrentPosition().getTopBlock().isFree()) {
                try {
                    moveLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            worker.getCurrentPosition().setWorkerOnTop(worker);
        } catch (NotFreeException e) {
            e.printStackTrace();
        }

        if(worker.getTimesMoved() > 0) {
            worker.setTimesMoved(worker.getTimesMoved() - 1);
        }

        // Waits for the subactions to be reverted by the created threads
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
