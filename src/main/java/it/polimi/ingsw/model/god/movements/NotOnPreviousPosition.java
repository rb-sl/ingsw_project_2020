package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Decorator class used to prevent a worker to move on the position it just left
 */
public class NotOnPreviousPosition extends BaseMovementDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public NotOnPreviousPosition(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canReach, inhibiting the return on a cell the worker has just left
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the base conditions are met and the worker isn't returning on its previous position
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        return super.canReach(worker, coordinates) && (worker.getTimesMoved() == 0 || !coordinates.equals(worker.getPlayer().getGod().getLogTurn().getLatestCoordinates()));
    }
}
