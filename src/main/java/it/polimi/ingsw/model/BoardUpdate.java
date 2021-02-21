package it.polimi.ingsw.model;

import java.util.List;

/**
 * Class used to log the changes applied to a map element during a turn
 */
public class BoardUpdate {
    /**
     * The list of blocks built on the cell
     */
    private List<Integer> levelList;
    /**
     * The (optional) player who owns the worker on top
     */
    private String workerOwner;
    /**
     * The sex of the optional worker
     */
    private Character workerSex;

    /**
     * Constructor for the class
     * @param levelList The cell's levels
     */
    public BoardUpdate(List<Integer> levelList) {
        this.levelList = levelList;
    }

    /**
     * Setter for the workerOwner attribute
     * @param workerOwner The nickname to be set
     */
    public void setWorkerOwner(String workerOwner) {
        this.workerOwner = workerOwner;
    }

    /**
     * Setter for the workerSex attribute
     * @param workerSex The sex to be set
     */
    public void setWorkerSex(Character workerSex) {
        this.workerSex = workerSex;
    }

    /**
     * Getter for the levels list
     * @return The list of levels on the cell
     */
    public List<Integer> getLevelList() {
        return levelList;
    }

    public void setLevelList(List<Integer> levelList) {
        this.levelList = levelList;
    }

    /**
     * Getter for the workerOwner attribute
     * @return The nickname of the worker's owner
     */
    public String getWorkerOwner() {
        return workerOwner;
    }

    /**
     * Getter for the workerSex attribute
     * @return The worker's sex
     */
    public char getWorkerSex() {
        return workerSex;
    }
}
