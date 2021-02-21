package it.polimi.ingsw.model.god.log;

import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.List;

/**
 * Class used to log and undo a build action
 */
public class LogBuild extends LogAction {
    /**
     * The object used to synchronize the revert of multiple build actions
     */
    private static final Object buildLock = new Object();
    /**
     * The worker connected to the action
     */
    private final Worker worker;

    /**
     * Constructor for the class
     * @param worker The worker who built
     * @param coordinates The coordinates destination of the build
     */
    public LogBuild(Worker worker, Coordinates coordinates) {
        super.setType(actionType.BUILD);
        this.worker = worker;
        super.setCoordinates(coordinates);
    }

    /**
     * Overloaded constructor with subactions
     * @param worker The worker who built
     * @param coordinates The coordinates destination of a build
     * @param subActions The subactions which happened before
     */
    public LogBuild(Worker worker, Coordinates coordinates, List<LogAction> subActions) {
        this(worker, coordinates);
        super.setSubActions(subActions);
    }

    /**
     * Reverts the construction of a block and all its subactions
     */
    @Override
    public void revert() {
        List<Thread> threadList = super.revertSubActions();

        synchronized (buildLock) {
            try {
                super.getCoordinates().removeTopBlock();
            } catch(WrongBlockException e) {
                e.printStackTrace();
            }
        }

        if(worker.getTimesBuilt() > 0) {
            worker.setTimesBuilt(worker.getTimesBuilt() - 1);
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
