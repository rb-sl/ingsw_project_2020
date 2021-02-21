package it.polimi.ingsw.model.map;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;

/**
 * Class that represents the different blocks, from level 0 to level 3
 */
public class Block extends MapComponent {
    /**
     * The constructor instantiates the block with the given level
     * @param level represents the level of the block 0-1-2-3
     */
    public Block(Integer level) {
        super(level);
    }

    /**
     * Checks if the block is free
     * @return True if free
     */
    @Override
    public boolean isFree() {
        if ((!hasWorkerOnTop()) && getTop() == 1)
            return true;
        else
            return false;
    }

    /**
     * Checks if the block has a worker on itself
     * @return True if the block has a worker on top
     */
    @Override
    public boolean hasWorkerOnTop() {
        if (this.getWorkerOnTop() == null)
            return false;
        else return true;
    }

    /**
     * Checks if the block is the top of its tower
     * @return True if the block is on top
     */
    @Override
    public boolean isTop() {
        if (getTop() == 1)
            return true;
        else
            return false;
    }

    /**
     * Sets a worker on top of the block
     * @param workerOnTop Worker to be set
     * @throws NotFreeException If the block is not free
     */
    @Override
    public void setWorkerOnTop(Worker workerOnTop) throws NotFreeException {
        if(this.hasWorkerOnTop())
            throw new NotFreeException();
        super.setWorkerOnTop(workerOnTop);
    }

    /**
     * Checks if the block can be built over
     * @return True if the block is buildable
     */
    @Override
    public boolean canBuildOnTop() {
        return this.isFree();
    }
}

