package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.exception.CantReachException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.god.log.LogTurn;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Class used as base movement behaviour
 */
public class BaseMovement implements GodMovement {
    /**
     * Checks if a worker can move to the given coordinates
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the worker can reach the coordinates
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        return worker.getPlayer().getWorkers().stream().map(w -> worker.equals(w) || w.getTimesMoved() == 0 && w.getTimesBuilt() == 0).reduce(true, (a,b) -> a && b)
                && worker.getTimesMoved() < worker.getPlayer().getGod().getMovableTimes()
                && (worker.getTimesBuilt() == 0)
                && worker.getCurrentPosition().isAdjacentTo(coordinates)
                && coordinates.getTopBlock().isFree()
                && (coordinates.heightDifference(worker.getCurrentPosition()) < 2);
    }

    /**
     * Default movement behaviour
     * @param worker The worker doing the move
     * @param coordinates The destination coordinates
     * @throws NotFreeException If the target MapComponent isn't free
     */
    @Override
    public void moveTo(Worker worker, Coordinates coordinates) throws NotFreeException, CantReachException {
        Coordinates previous;

        if(!worker.getPlayer().getGod().getMove().canReach(worker, coordinates)) {
            throw new CantReachException();
        }

        worker.setPreviousPosition(worker.getCurrentPosition());
        // Logging the previous position
        worker.getPlayer().getGod().getLogTurn().addMove(LogTurn.order.MAIN, worker, worker.getPreviousPosition());

        // Moving the worker to the new coordinates
        worker.setCurrentPosition(coordinates);
        worker.getCurrentPosition().setWorkerOnTop(worker);

        // Removing the worker from the previous position
        if((previous = worker.getPreviousPosition()) != null && worker.equals(previous.getTopBlock().getWorkerOnTop()))
            worker.getPreviousPosition().removeWorkerOnTop();
    }
}
