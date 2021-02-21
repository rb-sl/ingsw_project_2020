package it.polimi.ingsw.model.god.log;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.map.Coordinates;

/**
 * Class used to log and undo a worker place action
 */
public class LogPlaceWorker extends LogAction {
    /**
     * Constructor for the class
     * @param coordinates The coordinates destination of the build
     */
    public LogPlaceWorker(Coordinates coordinates) {
        super.setType(actionType.INIT);
        super.setCoordinates(coordinates);
    }

    /**
     * Reverts the placement of a worker
     */
    @Override
    public void revert() {
        Worker worker = super.getCoordinates().getTopBlock().getWorkerOnTop();
        Player owner = worker.getPlayer();

        owner.removeWorker(worker);
        super.getCoordinates().getTopBlock().removeWorkerOnTop();
    }
}
