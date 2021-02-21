package it.polimi.ingsw.model.map;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;

/**
 * Class that represents the different blocks, from level 0 to level 3, and the domes
 */
public abstract class MapComponent {
    /**
     * Flag that indicates if the block is the top one
     */
    private Integer top;
    /**
     * This field contains the worker over the block if exists
     */
    private Worker workerOnTop;
    /**
     * This field represents the block
     */
    private final Integer level;

    /**
     * Creator for the class
     * @param level The component's level
     */
    protected MapComponent(Integer level) {
        this.level = level;
        this.workerOnTop = null;
        this.top = 1;
    }

    /**
     * This method returns the status of the MapComponent
     * @return true if the MapComponent is free for movement and building
     */
    public abstract boolean isFree();

    /**
     * This method returns true if there is a worker over the MapComponent
     * @return true if the MapComponent has a worker over it, false if the attribute workerOnTop is null
     */
    public abstract boolean hasWorkerOnTop();

    /**
     * This method returns true if the MapComponent is eligible for building
     * @return true if a worker can build over it
     */
    public abstract boolean canBuildOnTop();

    /**
     * This method is a setter for the attribute workerOnTop
     * @param worker worker that is going to be over the map component
     * @throws NotFreeException if is not possible to set a worker over the MapComponent
     */
    public void setWorkerOnTop(Worker worker) throws NotFreeException {
        this.workerOnTop = worker;
    }

    /**
     * This method removes the worker over itself
     */
    public  void removeWorkerOnTop() {
        this.workerOnTop = null;
    }
    /**
     * This method is a getter for the attribute workerOnTop
     * @return the attribute workerOnTop
     */
    public  Worker getWorkerOnTop() {
        return this.workerOnTop;
    }

    /**
     * This method checks if the map component is the top one in his cell
     * @return true if top == 1 else return false
     */
    public abstract boolean isTop();

    /**
     * Setter for the top attribute
     * @param top parameter that is going to be set
     */
    public void setTop(Integer top) {
        this.top = top;
    }

    /**
     * Returns the component's level
     * @return The level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * Checks if the component is on top
     * @return True if the block is on top
     */
    public Integer getTop() {
        return top;
    }
}
