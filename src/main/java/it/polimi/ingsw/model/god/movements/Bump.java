package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.exception.CantReachException;
import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.god.log.LogTurn;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.model.map.MapComponent;

/**
 * Decorator class used to move on an opponent worker's position, forcing it on the opposed block
 */
public class Bump extends BaseMovementDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public Bump(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Checks if a worker can move to the given coordinates
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the worker can reach the coordinates
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        Coordinates opposed;
        // Checks that, if an opponent worker is on the target coordinates, the opposed coordinates are free
        return super.canReach(worker, coordinates) &&
                (!coordinates.getTopBlock().hasWorkerOnTop()
                        || (opposed = coordinates.getOpposedOf(worker.getCurrentPosition())) != null
                        && opposed.getTopBlock().isFree());
    }

    /**
     * Overridden moveTo, allows the worker to move onto an opponent worker's position forcing it in
     * the next position on the same direction
     * @param worker The worker doing the move
     * @param coordinates The destination coordinates
     * @throws CantReachException As per base behaviour or if the opponent's worker destination isn't free / doesn't exist
     * @throws HasWonException As per base behaviour
     * @throws NotFreeException As per base behaviour
     */
    @Override
    public void moveTo(Worker worker, Coordinates coordinates) throws CantReachException, HasWonException, NotFreeException {
        MapComponent target = coordinates.getTopBlock();

        if (target.hasWorkerOnTop()) {
            Coordinates opponentTargetCoordinates = coordinates.getOpposedOf(worker.getCurrentPosition());

            if (opponentTargetCoordinates != null && opponentTargetCoordinates.getTopBlock().isFree()) {
                Worker opponentWorker = target.getWorkerOnTop();
                target.removeWorkerOnTop();

                // If the moveTo can't be completed the situation is reverted
                try {
                    super.moveTo(worker, coordinates);
                } catch(CantReachException | HasWonException | NotFreeException e) {
                    target.setWorkerOnTop(opponentWorker);
                    throw e;
                }

                // Logging the opponent's position
                worker.getPlayer().getGod().getLogTurn().addMove(LogTurn.order.AFTER, opponentWorker, opponentWorker.getCurrentPosition());
                opponentWorker.forceTo(opponentTargetCoordinates);
            } else {
                throw new CantReachException();
            }
        }
        else {
            super.moveTo(worker, coordinates);
        }
    }
}
