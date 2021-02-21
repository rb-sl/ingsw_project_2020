package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.exception.CantReachException;
import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Decorator class used to inhibit the possibility of moving up of a player.
 * It's connected to the CantMoveUp class
 */
public class InhibitMoveUp extends BaseMovementDecorator {
    /**
     * String associated with the trigger
     */
    private static final String triggerName = "CantMoveUp";

    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public InhibitMoveUp(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden moveTo, controls the trigger for CantMoveUp
     * @param worker The worker doing the move
     * @param coordinates The destination coordinates
     * @throws CantReachException If the worker cannot reach the given coordinates
     * @throws HasWonException If the movement causes the player to win
     * @throws NotFreeException If the target MapComponent isn't free
     */
    @Override
    public void moveTo(Worker worker, Coordinates coordinates) throws CantReachException, HasWonException, NotFreeException  {
        super.moveTo(worker, coordinates);

        if(worker.getCurrentPosition().heightDifference(worker.getPreviousPosition()) > 0) {
            worker.getPlayer().getGod().getEventManager().notifyTrigger(triggerName, 1);
        }
    }
}
