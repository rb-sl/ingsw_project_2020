package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Decorator class used to inhibit the movement of a worker if it is higher of all others
 */
public class CantMoveHighest extends BaseMovementDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public CantMoveHighest(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canReach, blocks the movement of the higher worker
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the worker is not the highest one
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        for(Worker w: worker.getPlayer().getWorkers()) {
            if(!w.equals(worker) && worker.getCurrentPosition().heightDifference(w.getCurrentPosition()) > 0) {
                return false;
            }
        }
        return super.canReach(worker, coordinates);
    }
}
