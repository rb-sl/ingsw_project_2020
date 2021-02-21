package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Decorator class used to allow a worker to move after it built
 */
public class MoveAfterBuild extends BaseMovementDecorator {
    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public MoveAfterBuild(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canReach, allowing the move if the worker has already built
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the worker can reach the coordinates
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        boolean outcome;
        Integer timesBuilt = worker.getTimesBuilt();

        if(timesBuilt != 0) {
            // Hypothesizes that the worker never built
            worker.setTimesBuilt(0);

            outcome = super.canReach(worker, coordinates);

            // Reverts to the normal state
            worker.setTimesBuilt(timesBuilt);
        }
        else {
            outcome = super.canReach(worker, coordinates);
        }

        return outcome;
    }
}
