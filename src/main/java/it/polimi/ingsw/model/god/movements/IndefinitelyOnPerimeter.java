package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Decorator class allowing the move if the target is a perimeter space
 */
public class IndefinitelyOnPerimeter extends BaseMovementDecorator {
    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public IndefinitelyOnPerimeter(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canReach, allows the move if the target is on the perimeter
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the worker can reach the coordinates
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        boolean outcome;
        Integer timesMoved = worker.getTimesMoved();

        if(worker.getCurrentPosition().isOnPerimeter() && timesMoved != 0) {
            // Hypothesizes that the worker never moved, to check the other conditions
            worker.setTimesMoved(0);

            outcome = super.canReach(worker, coordinates);

            // Restores the moved times
            worker.setTimesMoved(timesMoved);
        }
        else {
            outcome = super.canReach(worker, coordinates);
        }

        return outcome;
    }
}
