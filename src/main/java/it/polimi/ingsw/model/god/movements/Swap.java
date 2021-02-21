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
 * Decorator class used to exchange position with another worker
 */
public class Swap extends BaseMovementDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public Swap(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden moveTo, switches position with another worker if necessary
     * @param worker The worker doing the move
     * @param coordinates The destination coordinates
     * @throws CantReachException If the worker cannot reach the given coordinates
     * @throws HasWonException If the movement causes the player to win
     * @throws NotFreeException If the target MapComponent isn't free
     */
    @Override
    public void moveTo(Worker worker, Coordinates coordinates) throws CantReachException, HasWonException, NotFreeException {
        Worker opponentWorker;
        MapComponent target = coordinates.getTopBlock();

        if (target.hasWorkerOnTop()) {
            // Removes the opponent from its position
            Coordinates workerOrigin = worker.getCurrentPosition();
            opponentWorker = target.getWorkerOnTop();
            target.removeWorkerOnTop();

            // If the moveTo can't be completed the situation is reverted
            try {
                super.moveTo(worker, coordinates);
            } catch(CantReachException | HasWonException | NotFreeException e) {
                target.setWorkerOnTop(opponentWorker);
                throw e;
            }

            // Completes the swap by moving the opponent to the worker's previous position
            opponentWorker.forceTo(workerOrigin);
            // Logging the opponent's previous position
            worker.getPlayer().getGod().getLogTurn().addMove(LogTurn.order.AFTER, opponentWorker, opponentWorker.getPreviousPosition());
        }
        else {
            super.moveTo(worker, coordinates);
        }
    }
}