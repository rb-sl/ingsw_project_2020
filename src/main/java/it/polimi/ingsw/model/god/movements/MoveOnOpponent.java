package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.model.map.MapComponent;

/**
 * Decorator class allowing a worker to reach a cell occupied by an opponent
 */
public class MoveOnOpponent extends BaseMovementDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public MoveOnOpponent(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canReach, allows reaching an opponent-occupied cell
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the base conditions are met or the coordinates host an opponent worker
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        boolean outcome = false;
        MapComponent targetBlock = coordinates.getTopBlock();
        if(targetBlock.hasWorkerOnTop()) {
            // Hypothesizes that the target coordinates are free to check other conditions
            Worker opponentWorker = targetBlock.getWorkerOnTop();
            if(!opponentWorker.getPlayer().getGod().equals(worker.getPlayer().getGod())) {
                targetBlock.removeWorkerOnTop();

                outcome = super.canReach(worker, coordinates);

                // Restores precedent conditions
                try {
                    targetBlock.setWorkerOnTop(opponentWorker);
                } catch (NotFreeException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            outcome = super.canReach(worker, coordinates);
        }

        return outcome;
    }
}
