package it.polimi.ingsw.model.god;

import it.polimi.ingsw.exception.CantReachException;
import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Interface used by the God class to define the movement behaviour of connected workers
 */
public interface GodMovement {
    /**
     * Determines if a worker can reach a specified position
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return A boolean value that specifies if the worker can reach the coordinates
     */
    public boolean canReach(Worker worker, Coordinates coordinates);

    /**
     * Moves a worker to the specified coordinates
     * @param worker The worker doing the move
     * @param coordinates The destination coordinates
     * @throws CantReachException If the destination MapComponent is out of range
     * @throws NotFreeException If the destination MapComponent is already occupied
     * @throws HasWonException If the worker has won with the current move
     */
    public void moveTo(Worker worker, Coordinates coordinates) throws CantReachException, NotFreeException, HasWonException;
}
