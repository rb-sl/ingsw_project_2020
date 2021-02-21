package it.polimi.ingsw.model.map;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;

/**
 * This class represents the dome
 */
public class Dome extends  MapComponent{
    /**
     * The constructor instantiates the dome
     */
    public Dome() {
        super(4);
    }

    /**
     * Overridden isFree
     * @return false
     */
    @Override
    public boolean isFree() {
        return false;
    }

    /**
     * Overridden hasWorkerOnTop
     * @return False
     */
    @Override
    public boolean hasWorkerOnTop() {
        return false;
    }

    /**
     * Overridden canBuildOnTop
     * @return False
     */
    @Override
    public boolean canBuildOnTop() {
        return false;
    }

    /**
     * Overridden setWorkerOnTop
     * @param workerOnTop The passed worker
     * @throws NotFreeException Always, as domes cannot host workers
     */
    @Override
    public void setWorkerOnTop(Worker workerOnTop) throws NotFreeException {
        throw new NotFreeException();
    }

    /**
     * Overridden isTop
     * @return True
     */
    @Override
    public boolean isTop() {
        return true;
    }

    /**
     * Overridden setTop, that cannot be changed
     */
    @Override
    public void setTop(Integer top) {}
}
