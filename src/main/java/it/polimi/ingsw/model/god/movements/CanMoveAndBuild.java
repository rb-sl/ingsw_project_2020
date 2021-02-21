package it.polimi.ingsw.model.god.movements;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Decorator class used when a connected power might cause the impossibility to build with the moved worker
 */
public class CanMoveAndBuild extends BaseMovementDecorator {
    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public CanMoveAndBuild(GodMovement wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canReach, checks that at least one of the coordinates near the destination are buildable through a
     * recursive function
     * @param worker The worker attempting the movement
     * @param coordinates The destination coordinates
     * @return True if the base conditions and the new ones are met
     */
    @Override
    public boolean canReach(Worker worker, Coordinates coordinates) {
        boolean hadWorker = false;
        Coordinates currentPosition = worker.getCurrentPosition();

        List<Coordinates> originPath = new ArrayList<>();
        originPath.add(currentPosition);

        // Removes (if needed, i.e. the first iteration) the worker from its position to check the possibility to build
        // on its current position
        if(currentPosition.getTopBlock().hasWorkerOnTop()) {
            hadWorker = true;
            currentPosition.getTopBlock().removeWorkerOnTop();
        }

        boolean outcome = recursiveReachable(worker, coordinates, originPath);

        // If necessary, restores the original position
        if(hadWorker) {
            try {
                currentPosition.getTopBlock().setWorkerOnTop(worker);
            } catch (NotFreeException e) {
                e.printStackTrace();
            }
        }

        return outcome;
    }

    /**
     * Checks that the worker can build from the destination coordinates. If not, it calls itself on each
     * coordinates adjacent to it, minus the ones representing the path back to the original worker's position.
     * Implements a recursive depth-first exploration of a graph
     * @param worker The worker attempting the move
     * @param currentCoordinates The coordinates being checked as build origin
     * @param originPath The list of coordinates not to be checked if the coordinates result not buildable, which have
     *                   been checked by previous iterations
     * @return True if the coordinates can be reached, either as final or intermediate move
     */
    private boolean recursiveReachable(Worker worker, Coordinates currentCoordinates, List<Coordinates> originPath) {
        boolean outcome = super.canReach(worker, currentCoordinates);

        if(outcome) {
            // Hypothesizes a move by placing the worker in the new position
            Coordinates previousPosition = worker.getPreviousPosition();
            Coordinates currentPosition = worker.getCurrentPosition();

            worker.setPreviousPosition(currentPosition);
            worker.setCurrentPosition(currentCoordinates);

            worker.setTimesMoved(worker.getTimesMoved() + 1);

            // A winning move is always allowed
            if (!worker.isWinner()) {
                List<Coordinates> adjacentCells = currentCoordinates.getAdjacentCells();
                Iterator<Coordinates> i = adjacentCells.iterator();

                // Checks that at least one position near the new coordinates is buildable
                outcome = false;
                while (!outcome && i.hasNext()) {
                    Coordinates c = i.next();
                    if (!worker.getPlayer().getGod().getBuild().canBuild(worker, c).isEmpty()) {
                        outcome = true;
                    }
                }

                // A negative outcome for the current coordinates doesn't imply that they cannot be
                // temporarily reached as part of a bigger move phase; checks the adjacent ones
                if (!outcome) {
                    i = adjacentCells.iterator();
                    // Updates the path leading to the next coordinates
                    List<Coordinates> newPath = new ArrayList<>(originPath);
                    newPath.add(currentCoordinates);

                    // Each adjacent cell (except the current one and its predecessors to avoid infinite loops) is
                    // checked for reachability through a recursive call
                    while (i.hasNext() && !outcome) {
                        Coordinates nextCoordinates = i.next();
                        if(!newPath.contains(nextCoordinates)) {
                            outcome = recursiveReachable(worker, nextCoordinates, newPath);
                        }
                    }
                }
            }

            // Restores the worker's previous properties and removes the worker from the checked coordinates
            worker.setPreviousPosition(previousPosition);
            worker.setCurrentPosition(currentPosition);
            worker.setTimesMoved(worker.getTimesMoved() - 1);
        }

        return outcome;
    }
}
