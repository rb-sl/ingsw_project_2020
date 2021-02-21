package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Decorator class used to prevent a worker from moving up if the relative trigger is active
 */
public class CantMoveUpTriggered extends BaseMovementDecorator {
    /**
     * String associated with the trigger
     */
    private static final String triggerName = "CantMoveUp";
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public CantMoveUpTriggered(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canReach, prevents moving up if the trigger is active
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return A boolean as per base behaviour, set false if the trigger is active and the cell is higher than the worker
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        Boolean trigger = worker.getPlayer().getGod().isTriggered(triggerName);
        return super.canReach(worker, coordinates) && (!trigger || coordinates.heightDifference(worker.getCurrentPosition()) <= 0);
    }
}
