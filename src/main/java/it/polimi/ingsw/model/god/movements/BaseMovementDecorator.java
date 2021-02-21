package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.exception.CantReachException;
import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Base decorator for the movement behaviour
 */
public abstract class BaseMovementDecorator implements GodMovement {
    /**
     * Wrapped object as per the decorator design pattern
     */
    private final GodMovement wrapped;

    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public BaseMovementDecorator(GodMovement wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Decorator for canReach, delegates the actions to the wrapped object
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the worker can move to the coordinates
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        return wrapped.canReach(worker, coordinates);
    }

    /**
     * Decorator for moveTo, delegates the actions to the wrapped object
     * @param worker The worker doing the move
     * @param coordinates The destination coordinates
     * @throws CantReachException If the worker cannot reach the given coordinates
     * @throws HasWonException If the movement causes the player to win
     * @throws NotFreeException If the target MapComponent isn't free
     */
    @Override
    public void moveTo(Worker worker, Coordinates coordinates) throws CantReachException, HasWonException, NotFreeException {
        wrapped.moveTo(worker, coordinates);
    }
}
